package com.graphqlForSpringboot.repository;

import org.springframework.stereotype.Repository;

import org.springframework.data.repository.CrudRepository;
import com.graphqlForSpringboot.models.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person,Integer>{

	Person findByEmail(String email);

}
