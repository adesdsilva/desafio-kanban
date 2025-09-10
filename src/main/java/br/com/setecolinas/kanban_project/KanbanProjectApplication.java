package br.com.setecolinas.kanban_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class KanbanProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(KanbanProjectApplication.class, args);
	}

}
