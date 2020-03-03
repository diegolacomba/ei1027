package es.uji.ei1027.majorsacasa.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import es.uji.ei1027.majorsacasa.model.Elderly;


@Repository
public class ElderlyDao {

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDateSource(DataSource dataSource) {
		jdbcTemplate=new JdbcTemplate(dataSource);
	}
	
	/* Afegeix al Elderly a la base de dades */
	public void addElderly(Elderly elderly) {
		jdbcTemplate.update("INSERT INTO SocialWorker VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", elderly.getDni(),
				elderly.getName(), elderly.getSurname(), elderly.getAddress(), elderly.getBankAccountNumber(), elderly.getUserpwd(), elderly.getEmail(), elderly.getPhoneNumber(), elderly.getBirthDate(), elderly.getDateCreation(), elderly.getAlergies(), elderly.getDiseases(), elderly.getUserCAS_socialWorker());
	}
	
	/* Esborra al Elderly de la base de dades */
	public void deleteElderly(String dni) {
		jdbcTemplate.update("DELETE from Elderly where dni=?", dni);
	}
	
	public void deleteElderly(Elderly elderly) {
		jdbcTemplate.update("DELETE from Elderly where dni=?", elderly.getDni());
	}
	
	 /* Actualitza els atributs del Elderly
    (excepte la clau primària) */
 public void updateElderly(Elderly elderly) {
     jdbcTemplate.update("UPDATE Elderly SET name=?, surname=?, address=?, bankAccountNumber=?, userpsw=?, email=?, phoneNumber=?, birthDate=?, dateCreation=?, alergies=?, diseases=?, userCAS_socialWorker=? WHERE dni=?",
    		 elderly.getName(), elderly.getSurname(), elderly.getAddress(), elderly.getBankAccountNumber(), elderly.getUserpwd(), elderly.getEmail(), elderly.getPhoneNumber(), elderly.getBirthDate(), elderly.getDateCreation(), elderly.getAlergies(), elderly.getDiseases(), elderly.getUserCAS_socialWorker(), elderly.getDni());
 }
	
 /* Obté el Elderly amb el nom donat. Torna null si no existeix. */
 public Elderly getElderly(String dni) {
     try {
         return jdbcTemplate.queryForObject("SELECT * FROM Elderly WHERE dni=?",
   		        new ElderlyRowMapper(),
    				dni);
     }
     catch(EmptyResultDataAccessException e) {
         return null;
     }
 }

 /* Obté tots els elderlys. Torna una llista buida si no n'hi ha cap. */
 public List<Elderly> getElderlys() {
     try {
         return jdbcTemplate.query("SELECT * FROM Elderly",
        	     new ElderlyRowMapper());
     }
     catch(EmptyResultDataAccessException e) {
         return new ArrayList<Elderly>();
     }
 }
}