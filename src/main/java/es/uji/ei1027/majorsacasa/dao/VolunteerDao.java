package es.uji.ei1027.majorsacasa.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import es.uji.ei1027.majorsacasa.model.Elderly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import es.uji.ei1027.majorsacasa.model.Volunteer;


@Repository
public class VolunteerDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDateSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix al Volunteer a la base de dades */
    public void addVolunteerAdmin(Volunteer volunteer) {
        jdbcTemplate.update("INSERT INTO Volunteer VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", volunteer.getName(), volunteer.getDni(), volunteer.getPhoneNumber(), volunteer.getEmail(),
                volunteer.getPwd(), volunteer.getHobbies(), volunteer.getApplicationDate(), volunteer.getAcceptationDate(), volunteer.getFinishDate(), volunteer.getAccepted(), volunteer.getBirthDate());
    }

    public void addVolunteerRegister(Volunteer volunteer) {
        LocalDate applicationDate = LocalDate.now();
        jdbcTemplate.update("INSERT INTO Volunteer VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", volunteer.getName(), volunteer.getDni(), volunteer.getPhoneNumber(), volunteer.getEmail(),
                volunteer.getPwd(), volunteer.getHobbies(), applicationDate, volunteer.getAcceptationDate(), volunteer.getFinishDate(), false, volunteer.getBirthDate());
    }

    /* Esborra al Volunteer de la base de dades */
    public void deleteVolunteer(String dni) {
        jdbcTemplate.update("DELETE from Volunteer where dni=?", dni);
    }

    public void deleteVolunteer(Volunteer volunteer) {
        jdbcTemplate.update("DELETE from Volunteer where dni=?", volunteer.getDni());
    }

    /* Actualitza els atributs del Volunteer
   (excepte la clau primària) */
    public void updateVolunteer(Volunteer volunteer) {
        jdbcTemplate.update("UPDATE Volunteer SET name=?, phoneNumber=?, email=?, pwd=?, hobbies=?, applicationDate=?, acceptationDate=?, finishDate=?, accepted=?, birthbDate=? WHERE dni=?",
                volunteer.getName(), volunteer.getPhoneNumber(), volunteer.getEmail(), volunteer.getPwd(), volunteer.getHobbies(),
                volunteer.getApplicationDate(), volunteer.getAcceptationDate(), volunteer.getFinishDate(), volunteer.getAccepted(), volunteer.getBirthDate(), volunteer.getDni());
    }

    /* Obté el Volunteer amb el nom donat. Torna null si no existeix. */
    public Volunteer getVolunteer(String dni) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Volunteer WHERE dni=?",
                    new VolunteerRowMapper(),
                    dni);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté tots els volunteers. Torna una llista buida si no n'hi ha cap. */
    public List<Volunteer> getVolunteers() {
        try {
            return jdbcTemplate.query("SELECT * FROM Volunteer ORDER BY dni",
                    new VolunteerRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<Volunteer>();
        }
    }

    /* Actualitza els atributs del Volunteer
(excepte la clau primària) */
    public void updateParaVolunteer(Volunteer volunteer) {
        jdbcTemplate.update("UPDATE Volunteer SET name=?, dni=?, pwd=?, email=?, phoneNumber=?, hobbies=?, birthDate=? WHERE dni=?",
                volunteer.getName(), volunteer.getDni(), volunteer.getPwd(), volunteer.getEmail(), volunteer.getPhoneNumber(), volunteer.getHobbies(), volunteer.getBirthDate(),volunteer.getDni());
    }

    public void approveVolunteer(String dni) {
        LocalDate today = LocalDate.now();

        jdbcTemplate.update("UPDATE Volunteer SET accepted=?, acceptationDate=? WHERE dni=?", true, today, dni);
    }

    public void rejectVolunteer(String dni) {
        LocalDate today = LocalDate.now();

        jdbcTemplate.update("UPDATE Volunteer SET accepted=?, finishDate=? WHERE dni=?", false, today, dni);
    }
}