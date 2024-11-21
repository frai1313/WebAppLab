package com.dipartimento.demowebapplications.persistence.dao.impljdbc;

import com.dipartimento.demowebapplications.model.Piatto;
import com.dipartimento.demowebapplications.model.Ristorante;
import com.dipartimento.demowebapplications.persistence.DBManager;
import com.dipartimento.demowebapplications.persistence.dao.PiattoDao;
import com.dipartimento.demowebapplications.persistence.dao.RistoranteDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RistoranteDaoJDBC implements RistoranteDao {
    Connection connection = null;

    public RistoranteDaoJDBC(Connection conn){

        this.connection = conn;
    }

    @Override
    public List<Ristorante> findAll() {
        List<Ristorante> ristoranti = new ArrayList<Ristorante>();
        String query = "select * from ristorante";

        System.out.println("going to execute:"+query);

        Statement st = null;
        try {
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                Ristorante rist = new Ristorante();
                rist.setNome(rs.getString("nome"));
                rist.setDescrizione(rs.getString("descrizione"));
                rist.setUbicazione(rs.getString("ubicazione"));
                ristoranti.add(rist);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ristoranti;
    }

    @Override
    public Ristorante findByPrimaryKey(String nome) {

        String query = "SELECT nome, descrizione, ubicazione FROM ristorante WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nome);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String descrizione = resultSet.getString("descrizione");
                String ubicazione = resultSet.getString("ubicazione");
                Ristorante rist = new Ristorante();
                rist.setNome(nome);
                rist.setDescrizione(descrizione);
                rist.setUbicazione(ubicazione);
                return  rist;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void create(Ristorante ristorante) {
        String query = "INSERT INTO ristorante (nome, descrizione, ubicazione) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristorante.getNome());
            statement.setString(2, ristorante.getDescrizione());
            statement.setString(3, ristorante.getUbicazione());
            statement.executeUpdate();

            List<Piatto> piatti = ristorante.getPiatti();
            if (piatti != null && !piatti.isEmpty()) {
                PiattoDao piattoDao = DBManager.getInstance().getPiattoDao();
                for (Piatto piatto : piatti) {
                    // Salva il piatto (se non esiste già)
                    piattoDao.save(piatto);

                    // Inserisce la relazione nella tabella di join
                    insertJoinRistorantePiatto(connection, ristorante.getNome(), piatto.getNome());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertJoinRistorantePiatto(Connection connection , String nomeRistorante, String nomePiatto) throws SQLException {

        String query="INSERT INTO ristorante_piatto (ristorante_nome,piatto_nome) VALUES (? , ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, nomeRistorante);
        preparedStatement.setString(2, nomePiatto);

        preparedStatement.execute();
    }

    @Override
    public void save(Ristorante ristorante) {
        String query = "INSERT INTO ristorante (nome, descrizione, ubicazione) VALUES (?, ?, ?) " +
                "ON CONFLICT (nome) DO UPDATE SET " +
                "   descrizione = EXCLUDED.descrizione , "+
                "   ubicazione = EXCLUDED.ubicazione ";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristorante.getNome());
            statement.setString(2, ristorante.getDescrizione());
            statement.setString(3, ristorante.getUbicazione());
            statement.executeUpdate();

            List<Piatto> piatti = ristorante.getPiatti();
            if(piatti==null || piatti.isEmpty()){
                return;
            }
            // reset all relation present in the join table
            restRelationsPResentInTheJoinTable(connection , ristorante.getNome());

            PiattoDao pd = DBManager.getInstance().getPiattoDao();

            for (Piatto tempP : piatti) {
                pd.save(tempP);
                insertJoinRistorantePiatto(connection , ristorante.getNome() , tempP.getNome());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restRelationsPResentInTheJoinTable(Connection connection, String nomeRistorante) {
        String query = "DELETE FROM ristorante_piatto WHERE ristorante_nome = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeRistorante);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la cancellazione delle relazioni nella tabella di join.", e);
        }
    }

    @Override
    public void delete(Ristorante ristorante) {
        String query = "DELETE FROM ristorante WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Cancella le relazioni nella tabella di join
            restRelationsPResentInTheJoinTable(connection, ristorante.getNome());

            // Cancella il ristorante
            statement.setString(1, ristorante.getNome());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la cancellazione del ristorante.", e);
        }
    }

    @Override
    public List<Ristorante> findRistorantiByPiattoName(String nomePiatto) {
        List<Ristorante> ristoranti = new ArrayList<>();
        String query = "SELECT r.nome, r.descrizione, r.ubicazione " +
                "FROM ristorante r " +
                "JOIN ristorante_piatto rp ON r.nome = rp.ristorante_nome " +
                "WHERE rp.piatto_nome = ?";

        System.out.println("Going to execute: " + query);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nomePiatto);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String ristoranteNome = resultSet.getString("nome");
                String ristoranteDescrizione = resultSet.getString("descrizione");
                String ristoranteUbicazione = resultSet.getString("ubicazione");

                Ristorante ristorante = new Ristorante();
                ristorante.setNome(ristoranteNome);
                ristorante.setDescrizione(ristoranteDescrizione);
                ristorante.setUbicazione(ristoranteUbicazione);

                ristoranti.add(ristorante);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ristoranti;
    }

    public static void main(String[] args) {
        RistoranteDao ristoDao = DBManager.getInstance().getRistoranteDao();
        List<Ristorante> ristoranti = ristoDao.findAll();
        for (Ristorante ristorante : ristoranti) {
            System.out.println(ristorante.getNome());
            System.out.println(ristorante.getDescrizione());
            System.out.println(ristorante.getUbicazione());
        }
    }
}


