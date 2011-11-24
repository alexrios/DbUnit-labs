package labs.alex.dbunit.repository.jdbc;

import java.io.File;

import javax.sql.DataSource;

import labs.alex.dbunit.domain.Pessoa;
import labs.alex.dbunit.repository.PessoaRepository;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/repository-test-context.xml"})
public class JdbcPessoaRepositoryTest {
	
	@Autowired
	PessoaRepository repository;
	
	@Autowired
	SimpleJdbcTemplate jdbcTemplate;
	
	@Autowired
	DataSource dataSource;
	
	@Before public void setUp() throws Exception {
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(new File(
			"./src/test/resources/dataset.xml"
		));
		IDatabaseConnection dbConn = new DatabaseDataSourceConnection(dataSource);
		DatabaseConfig config = dbConn.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
		DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
	}
	
	@Test public void selectByLastname() {
		assertEquals(0,repository.selectBySobrenome("cogo").size());
		assertEquals(1,repository.selectBySobrenome("our").size());
		assertEquals(3,repository.selectBySobrenome("o").size());
	}

	@Test public void create() {
		int initialCount = jdbcTemplate.queryForInt("select count(1) from person");
		Pessoa alexRios = new Pessoa("Alex", "Rios");
		repository.create(alexRios);
		assertEquals(initialCount+1,jdbcTemplate.queryForInt("select count(1) from person"));
	}
	
	@Test public void update() {
		Pessoa pessoa = new Pessoa(-3L,"Zack", "Dolphin");
		repository.update(pessoa);
		assertEquals(repository.selectBySobrenome("Dolphin").get(0), pessoa);
	}
	
	
	@Test public void remove() {
		int initialCount = jdbcTemplate.queryForInt("select count(1) from person");
		repository.remove(-3L);
		assertEquals(initialCount-1, jdbcTemplate.queryForInt("select count(1) from person"));
	}
	
	
	@Test public void queryOnSysDummy1() {
		int one = jdbcTemplate.queryForInt("select 1 from sysibm.sysdummy1");
		assertEquals(1, one);
	}
	
	@Test public void commonTableExpression() {
		int one = jdbcTemplate.queryForInt("with Q as (select count(1) from person) select 1 from Q");
		assertEquals(1, one);
	}
}