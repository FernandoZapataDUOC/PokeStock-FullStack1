package com.pokestock.ms_documentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsDocumentosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsDocumentosApplication.class, args);
	}

}
