package es.uji.ei1027.majorsacasa.dao;

import es.uji.ei1027.majorsacasa.model.Elderly;
import es.uji.ei1027.majorsacasa.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDao {


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDateSource(DataSource dataSource) {
        jdbcTemplate=new JdbcTemplate(dataSource);
    }

    /* insert new invoice into the data */
    public void addInvoice(Invoice invoice) {
        jdbcTemplate.update("INSERT INTO Invoice VALUES(?, ?, ?, ?, ?)",
                invoice.getFecha(), invoice.getIdNumber(), invoice.getConcept(), invoice.getAmount(), invoice.getElderlyDNI());
    }

    /* delete invoice from db */
    public void deleteInvoice(int idNumber) {
        jdbcTemplate.update("DELETE from Invoice where id=?", idNumber);
    }

    public void deleteInvocie(Invoice invoice) {
        jdbcTemplate.update("DELETE from Invoice where ID=?", invoice.getIdNumber());
    }

    /* update invoice atribute excep primary key*/
    public void updateInvoice(Invoice invoice) {
        jdbcTemplate.update("UPDATE Invoice SET fecha=?, idNumber=?, amount=?, concept=?, dni_elderly=?",
                invoice.getFecha(),invoice.getIdNumber(),invoice.getAmount(),invoice.getConcept(),invoice.getElderlyDNI());
    }

    /* try
    *       Get selected invoice
    *        @return NULL if not exist */
    public Invoice getInvoice(String id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM invoice WHERE Id=?",
                    new InvoiceRowMapper(),
                    id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* get all invoice form db
    * @return empty list if there is not any Invoice  */
    public List<Invoice> getElderlys() {
        try {
            return jdbcTemplate.query("SELECT * FROM Invoice",
                    new InvoiceRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Invoice>();
        }

    }

    /* get all invoice from a elderly */
    /* @return
    */
     public List<Invoice> getInvoicesByElderly(String dni){
         try {
             return jdbcTemplate.query("SELECT * FROM Invoice WHERE dni_elderly=?",
                     new InvoiceRowMapper(), dni);
         }
         catch(EmptyResultDataAccessException e) {
             return new ArrayList<Invoice>();
         }
     }






}