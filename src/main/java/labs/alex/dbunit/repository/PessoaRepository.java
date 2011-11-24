package labs.alex.dbunit.repository;

import java.util.List;

import labs.alex.dbunit.domain.Pessoa;


public interface PessoaRepository {

	Pessoa create(Pessoa pessoa);
	
	Pessoa update(Pessoa pessoa);
	
	List<Pessoa> selectBySobrenome(String sobrenome);

	List<Pessoa> selectById(Long id);

	void remove(Long id);
}
