package com.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

// spring-boot:1.3.7.RELEASE
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = {App.class})
//@TestExecutionListeners({
//		DependencyInjectionTestExecutionListener.class
//})
// spring-boot:1.4.0.RELEASE
@RunWith(SpringRunner.class)
@SpringBootTest
public class DistinctAndNewTest {

	@Autowired
	PersonRepository personRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Before
	public void prepare() {

		personRepository.deleteAll();
		departmentRepository.deleteAll();

		{
			Department d = departmentRepository.save(new Department("abc"));
			personRepository.save(new Person("abc_1", PersonType.FOO, d));
			personRepository.save(new Person("abc_2", PersonType.BAR, d));
			personRepository.save(new Person("abc_3", PersonType.FOO, d));
		}
		{
			Department d = departmentRepository.save(new Department("def"));
			personRepository.save(new Person("def_1", PersonType.BAR, d));
			personRepository.save(new Person("def_2", PersonType.FOO, d));
			personRepository.save(new Person("def_3", PersonType.BAR, d));
		}
	}

	@Test
	public void new_by_datajpa() {

		List<Pair> actual = departmentRepository.findByPersonName1("%abc%");
		assertThat(actual, iterableWithSize(3));
	}

	@Test
	public void new_and_distinct_by_datajpa() {

		List<Pair> actual = departmentRepository.findByPersonName2("%abc%");
		assertThat(actual, iterableWithSize(1));
	}

	@Test
	public void new_and_distinct_by_criteria() {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Pair> query = cb.createQuery(Pair.class);
		Root<Department> root = query.from(Department.class);

		Join<Object, Object> jPersons = root.join("persons");
		query.distinct(true);
		query.select(cb.construct(Pair.class, root.get("id"), root.get("name")));
		query.where(cb.like(jPersons.get("name"), "%abc%"));

		List<Pair> actual = entityManager.createQuery(query).getResultList();
		assertThat(actual, iterableWithSize(1));
	}

	@Test
	public void new_and_distinct_by_hql() {

		List<Pair> actual = entityManager.createQuery("" +
				"select distinct new com.example.Pair(d.id, d.name) " +
				"from Department d join d.persons p " +
				"where p.name like ?1", Pair.class)
				.setParameter(1, "%abc%")
				.getResultList();

		assertThat(actual, iterableWithSize(1));
	}
}
