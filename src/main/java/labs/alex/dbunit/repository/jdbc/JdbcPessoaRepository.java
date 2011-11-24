package labs.alex.dbunit.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import labs.alex.dbunit.domain.Pessoa;
import labs.alex.dbunit.repository.PessoaRepository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class JdbcPessoaRepository implements PessoaRepository {
	
	private SimpleJdbcTemplate jdbcTemplate;
	
	public JdbcPessoaRepository(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	public Pessoa create(Pessoa pessoa) {
		long nextId = jdbcTemplate.queryForLong("select nextval('person_seq')");
		jdbcTemplate.update(
			"insert into person (id,firstname,lastname) values (?,?,?)",
			nextId,pessoa.getNome(),pessoa.getSobrenome()
		);
		pessoa.setId(nextId);
		return pessoa;
	}
	
	public Pessoa update(Pessoa pessoa) {
		jdbcTemplate.update(
				"update person set firstname = ?, lastname = ? where id = ?",
				pessoa.getNome(),pessoa.getSobrenome(),pessoa.getId()
			);
		return pessoa;
	}
	
	public void remove(Long id) {
		jdbcTemplate.update(
				"delete from person where id = ?",
				id
			);
		return ;
	}

	public List<Pessoa> selectBySobrenome(String lastname) {		
		return jdbcTemplate.query(
			"select * from person where lastname like ?",
			new PessoaRowMapper(),
			"%"+lastname+"%"
		);
	}
	
	public List<Pessoa> selectById(Long id) {		
		return jdbcTemplate.query(
			"select * from person where id = ?",
			new PessoaRowMapper(),
			id
		);
	}
	
	private static class PessoaRowMapper implements RowMapper<Pessoa> {
		
		public Pessoa mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Pessoa(rs.getLong("id"), rs.getString("firstname"),rs.getString("lastname"));
		}
		
	}

}
