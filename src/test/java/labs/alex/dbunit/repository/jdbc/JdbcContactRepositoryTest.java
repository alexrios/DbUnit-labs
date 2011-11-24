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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/repository-test-context.xml"	
})
public class JdbcContactRepositoryTest {
	
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
		Assert.assertEquals(0,repository.selectBySobrenome("cogo").size());
		Assert.assertEquals(1,repository.selectBySobrenome("our").size());
		Assert.assertEquals(3,repository.selectBySobrenome("o").size());
	}

	@Test public void create() {
		int initialCount = jdbcTemplate.queryForInt("select count(1) from person");
		Pessoa alexRios = new Pessoa("Alex", "Rios");
		repository.create(alexRios);
		Assert.assertEquals(initialCount+1,jdbcTemplate.queryForInt("select count(1) from person"));
	}
	
	@Test public void update() {
		Pessoa pessoa = new Pessoa(-3L,"Zack", "Dolphin");
		repository.update(pessoa);
		Assert.assertEquals(repository.selectBySobrenome("Dolphin").get(0), pessoa);
	}
	
	
	@Test public void remove() {
		int initialCount = jdbcTemplate.queryForInt("select count(1) from person");
		repository.remove(-3L);
		Assert.assertEquals(initialCount-1, jdbcTemplate.queryForInt("select count(1) from person"));
	}
	
}
