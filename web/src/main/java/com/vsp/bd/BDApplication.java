package com.vsp.bd;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAsync
public class BDApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BDApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(BDApplication.class, args);
	}

	@Bean
	public LocaleResolver localeResolver() {
		FixedLocaleResolver fixedLocaleResolver = new FixedLocaleResolver();
		fixedLocaleResolver.setDefaultLocale(new Locale("ro"));
		return fixedLocaleResolver;
	}

	@Bean
	@Description("Thymeleaf view resolver")
	public ThymeleafViewResolver thymeleafViewResolver(final SpringTemplateEngine templateEngine) {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine);
		resolver.setCharacterEncoding("UTF-8");
		resolver.setCache(false);
		resolver.setOrder(1);
		return resolver;
	}
}
