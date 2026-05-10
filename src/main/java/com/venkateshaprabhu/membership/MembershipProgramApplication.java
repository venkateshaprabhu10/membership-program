package com.venkateshaprabhu.membership;

import com.venkateshaprabhu.membership.config.BrandConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BrandConfig.class)
public class MembershipProgramApplication {

	public static void main(String[] args) {
		SpringApplication.run(MembershipProgramApplication.class, args);
	}

}



