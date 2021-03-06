package es.uji.ei1027.majorsacasa.dao;

import es.uji.ei1027.majorsacasa.model.Company;
import es.uji.ei1027.majorsacasa.model.Contract;
import es.uji.ei1027.majorsacasa.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Repository
public class ContractDao {


    private JdbcTemplate jdbcTemplate;
    private CompanyDao companyDao;
    private int id = 1;

    @Autowired
    public void setDateSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public void setCompanyDao(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    /* insert new contract into the data */
    public void addContract(Contract contract) {
        jdbcTemplate.update("INSERT INTO Contract VALUES(?, ?, ?, ?, ?, ?)",
                contract.getIdNumber(), contract.getDateBegining(), contract.getDateEnding(), contract.getServiceType(), contract.getPrice(), contract.getCif_company());
    }

    /* delete contract from db */
    public void deleteContract(String idNumber) {
        jdbcTemplate.update("DELETE from Contract where idNumber=?", idNumber);
    }

    public void deleteContract(Contract contract) {
        jdbcTemplate.update("DELETE from Contract where idNumber=?", contract.getIdNumber());
    }

    /* update invoice contract excep primary key*/
    public void updateContract(Contract contract) {
        jdbcTemplate.update("UPDATE Contract SET idNumber=?, dateBegining=?, dateEnding=?, serviceType=?, price=?, cif_company=?",
                contract.getIdNumber(), contract.getDateBegining(), contract.getDateEnding(), contract.getServiceType(), contract.getPrice(), contract.getCif_company());
    }


    /* Obté el Elderly amb el nom donat. Torna null si no existeix. */
    public Contract getContract(String idNumber) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Contract WHERE idNumber=?",
                    new ContractRowMapper(),
                    idNumber);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté tots els elderlys. Torna una llista buida si no n'hi ha cap. */
    public List<Contract> getContracts() {
        try {
            return jdbcTemplate.query("SELECT * FROM Contract ORDER BY idNumber",
                    new ContractRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<Contract>();
        }
    }

    /*Afegeix a les request recientment aprovades un contracte) */
    public void contractRequest(Request request) {
        LocalDate today = LocalDate.now();

        //hacemos una lista de Contracts que proporcionen el servici desitjat
        List<Contract> listaContractsServici = new LinkedList<>();
        for (Contract con : this.getContracts()) {
            if (request.getServiceType().equals(con.getServiceType())) {
                listaContractsServici.add(con);
            }
        }
        Random rand = new Random();
        Contract contract = listaContractsServici.get(rand.nextInt(listaContractsServici.size()));

        jdbcTemplate.update("UPDATE Request SET idNumber_contract=? WHERE idNumber=?", contract.getIdNumber(), request.getIdNumber());
    }
}
