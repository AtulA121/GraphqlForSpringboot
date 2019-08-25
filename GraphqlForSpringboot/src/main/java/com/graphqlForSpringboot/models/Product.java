package com.graphqlForSpringboot.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@Setter
@Getter
@Entity
public class Product {
	@Id
	private int id;
	private String name;
	private String mobile;
	private String email;
	private String[] address;
	
	
}
