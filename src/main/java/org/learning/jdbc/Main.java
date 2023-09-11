package org.learning.jdbc;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
  // parametri di connessione al db
  private final static String DB_URL = "jdbc:mysql://localhost:3306/jaita91_nations";
  private final static String DB_USER = "root";
  private final static String DB_PASSWORD = "rootpassword";

  // query
  private final static String SQL_NATIONS = """
      SELECT c.name as country_name, c.country_id, r.name as region_name, cn.name as continent_name
      FROM countries c
      JOIN regions r ON c.region_id = r.region_id
      JOIN continents cn ON r.continent_id = cn.continent_id
      ORDER BY c.name;
      """;

  private final static String SQL_NATIONS_SEARCH = """
      SELECT c.name as country_name, c.country_id, r.name as region_name, cn.name as continent_name
      FROM countries c
      JOIN regions r ON c.region_id = r.region_id
      JOIN continents cn ON r.continent_id = cn.continent_id
      WHERE c.name LIKE ?
      ORDER BY c.name;""";

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);

    // chiedo all'utente la stringa di ricerca
    System.out.print("Search: ");
    String search = scan.nextLine();


    try(Connection connection = DriverManager.getConnection(DB_URL,DB_USER, DB_PASSWORD)){
      // System.out.println(connection.getCatalog());
      try(PreparedStatement ps = connection.prepareStatement(SQL_NATIONS_SEARCH)){
        // sostituisco i ? con i parametri veri (binding)
        ps.setString(1, "%" +search + "%");

        try(ResultSet rs = ps.executeQuery()){
          while(rs.next()){
            // leggo i valori nelle singole colonne della riga corrente
            String countryName = rs.getString("country_name");
            int countryId = rs.getInt("country_id");
            String regionName = rs.getString("region_name");
            String continentName = rs.getString("continent_name");
            // stampo a video i dati delle country
            System.out.println(countryId + " | " + countryName + " | " + regionName + " | " + continentName);

          }
        }catch(SQLException e){
          System.out.println("Unable to execute query");
          e.printStackTrace();
        }
      } catch(SQLException e){
        System.out.println("Unable to prepare statement SQL");
      }

    } catch(SQLException e){
      System.out.println("Unable to connect to database");
      // a scopo di debug chiedo all'eccezione di stampare in console lo stack dell'errore
      // e.printStackTrace();
    }

    scan.close();
  }
}
