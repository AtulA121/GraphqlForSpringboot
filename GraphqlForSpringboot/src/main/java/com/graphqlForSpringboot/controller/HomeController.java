package com.graphqlForSpringboot.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.graphqlForSpringboot.models.Person;
import com.graphqlForSpringboot.repository.PersonRepository;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@RestController
public class HomeController {
	
	@Value("classpath:person.graphqls")
	Resource shcemaResource;
	
	@Autowired
	PersonRepository personRepository;
	
	private GraphQL graphql;
	
	@PostConstruct
	public void loadSchema() throws IOException
	{
		File schemaFile=shcemaResource.getFile();
		TypeDefinitionRegistry registry=new SchemaParser().parse(schemaFile);
		RuntimeWiring wiring=buildWiring();
		GraphQLSchema schema=new SchemaGenerator().makeExecutableSchema(registry, wiring);
		graphql=GraphQL.newGraphQL(schema).build();
	}
	
	public RuntimeWiring buildWiring()
	{
		DataFetcher<List<Person>> fetcher1=data->{
			return (List<Person>) personRepository.findAll();
		};
		
		DataFetcher<Person> fetcher2=data->{
			System.out.println(""+data.getArgument("email"));
			return personRepository.findByEmail(data.getArgument("email"));
		};
		
		return RuntimeWiring.newRuntimeWiring().type("Query",typeWiring->
			typeWiring.dataFetcher("getAllPerson",fetcher1).dataFetcher("findPersonByEmail",fetcher2))
			.build();
	}
	
	@PostMapping("/getAll")
	public ResponseEntity<?> getAll(@RequestBody String query)
	{
		ExecutionResult result=graphql.execute(query);
		return new ResponseEntity<Object>(result,HttpStatus.OK);
	}
	
	@PostMapping("/getPersonByEmail")
	public ResponseEntity<?> getPersonByEmail(@RequestBody String query)
	{
		ExecutionResult result=graphql.execute(query);
		return new ResponseEntity<Object>(result,HttpStatus.OK);
	}
	
	@PostMapping("/findAllPersonByEmail")
	public ResponseEntity<?> findAllPersonByEmail(@RequestParam("email") String email)
	{
		String query="{"+
//				"findPersonByEmail(email:\""+email+"\") {"+
				"findPersonByEmail(email : \""+email+"\") {"+
				"name "+
				"address "+
				"mobile "+
				"email"+
				"}"+
			"}";
		ExecutionResult result=graphql.execute(query);
		return new ResponseEntity<Object>(result,HttpStatus.OK);
	}
	
	@PostMapping("/findAllPersonByEmail2")
	public ResponseEntity<?> findAllPersonByEmail2(@RequestBody String query)
	{
		System.out.println("your query : "+query);
//		String query="";
		ExecutionResult result=graphql.execute(query);
		return new ResponseEntity<Object>(result,HttpStatus.OK);
	}
	//query should like this
	/*{
		findPersonByEmail(email : "atulpisal.ap@gmail.com") {
				name
				email
			}
		}
	*/
	
	
	@PostMapping("/addPerson")
	public ResponseEntity<?> addPerson(@RequestBody List<Person> personList)
	{
		personRepository.saveAll(personList);
		return new ResponseEntity<>("inserted successfully : "+personList.size(),HttpStatus.OK);
	}
	
	@GetMapping("/findAllPerson")
	public List<Person> findAllPerson()
	{
		return (List<Person>) personRepository.findAll();
	}
}
