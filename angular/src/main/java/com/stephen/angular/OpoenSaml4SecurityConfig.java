package com.stephen.angular;
import java.io.File;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opensaml.security.x509.X509Support;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OpoenSaml4SecurityConfig {
	
	@Autowired
	private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;
	
	@Bean
	public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
		RelyingPartyRegistrationResolver relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);

	Saml2MetadataFilter filter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());
		OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(groupsConverter());
        
				http
				.authorizeHttpRequests(authorize ->authorize.requestMatchers("/hello").permitAll()
						.anyRequest().authenticated())
				.saml2Login(saml2->saml2.authenticationManager(new ProviderManager(authenticationProvider)));

				
				http.addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class);				
				return http.build();
	}
	@Bean
	public RelyingPartyRegistrationRepository relyingPartyRegistrations() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		String filepath=classLoader.getResource("saml-certificate/saml.cer").getFile();
		//if folder name contains space we need to remove the space .. or otherwise we got File not found Exception
		 String decodedPath = URLDecoder.decode(filepath, "UTF-8");
		File verificationKey = new File(decodedPath);
	    X509Certificate certificate = X509Support.decodeCertificate(verificationKey);
	    Saml2X509Credential credential = Saml2X509Credential.verification(certificate);
	    
	    RelyingPartyRegistration relyingPartyRegistration = RelyingPartyRegistrations
                .fromMetadataLocation("https://login.microsoftonline.com/6c251faa-cda2-4e44-9ff0-f2ace48d9131/federationmetadata/2007-06/federationmetadata.xml?appid=e991cd1c-fc15-4800-85ac-aba99d5d9fc9")
                .registrationId("azure")
                .entityId("azureentityid")
              // .assertionConsumerServiceLocation("{baseUrl}/")
                .assertingPartyDetails(party -> party
                		//Microsoft Entra Identifier
    	                .entityId("https://sts.windows.net/6c251faa-cda2-4e44-9ff0-f2ace48d9131/")
    	                //Login URL
    	                .singleSignOnServiceLocation("https://login.microsoftonline.com/6c251faa-cda2-4e44-9ff0-f2ace48d9131/saml2")
    	                .wantAuthnRequestsSigned(false)
    	                .verificationX509Credentials(c -> c.add(credential))
    	            ).build();
     
	    return new InMemoryRelyingPartyRegistrationRepository(relyingPartyRegistration);
	}
	
	 private Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> groupsConverter() {

	        Converter<ResponseToken, Saml2Authentication> delegate =
	            OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();

	        return (responseToken) -> {
	            Saml2Authentication authentication = delegate.convert(responseToken);
	            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
	            List<String> groups = principal.getAttribute("groups");
	            Set<GrantedAuthority> authorities = new HashSet<>();
	            if (groups != null) {
	                groups.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
	            } else {
	                authorities.addAll(authentication.getAuthorities());
	            }
	            return new Saml2Authentication(principal, authentication.getSaml2Response(), authorities);
	        };
	    }
	 
	 
	

}

