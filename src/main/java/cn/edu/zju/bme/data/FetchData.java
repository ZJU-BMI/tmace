package cn.edu.zju.bme.data;

import cn.edu.zju.bme.util.ManageConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static cn.edu.zju.bme.util.ManageConnection.close;

public class FetchData {

    private String url = "jdbc:oracle:thin:@172.16.200.24:1521:plaacs";
    private String username = "DATA_SOURCE";
    private String password = "DATA_SOURCE";
    private Connection connection = null;



    public FetchData() {
        init();
    }

    public FetchData(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        init();
    }

    private void init() {

    }

    public void fetchAndSave(String rootPath) {
        String selectPatient = "SELECT * FROM DATA_SOURCE.PATIENT";
        String selectVisit = "SELECT * FROM DATA_SOURCE.VISIT WHERE PATIENT_ID = ?";

        connection = ManageConnection.getConnection(url, username, password);

        try {
            PreparedStatement patientStatement = connection.prepareStatement(selectPatient);
            ResultSet patientResult = patientStatement.executeQuery();

            while (patientResult.next()) {
                String patientId = patientResult.getString("PATIENT_ID");
                PreparedStatement visitStatement = connection.prepareStatement(selectVisit);
                visitStatement.setString(1, patientId);
                ResultSet visitResult = visitStatement.executeQuery();
                while (visitResult.next()) {
                    saveOne(visitResult, patientId, rootPath);
                }
                close(visitResult);
                close(visitStatement);
            }

            close(patientResult);
            close(patientStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection);
        }
    }

    private void saveOne(ResultSet visitResult, String patientId, String rootPath) {
        try {
            String visitId = visitResult.getString("VISIT_ID");
            String selectEMR = "SELECT * FROM DATA_SOURCE.EMR WHERE PATIENT_ID = ? AND VISIT_ID = ?";

            PreparedStatement emrStatement = connection.prepareStatement(selectEMR);
            emrStatement.setString(1, patientId);
            emrStatement.setString(2, visitId);
            ResultSet emrResult = emrStatement.executeQuery();

            while (emrResult.next()) {
                String memo = emrResult.getString("MEMO");
                if (memo.contains("病程")) {
                    String emr = emrResult.getString("EMR_CONTEXT_XML");
                    if (emr == null) continue;
                    String emrId = emrResult.getString("EMR_ID");
                    BufferedWriter out = new BufferedWriter(new FileWriter(new File(rootPath+"/"+patientId+"_"+visitId+"_"+emrId+".html")));
                    out.write(emr);
                    out.flush();
                    out.close();
                }
            }

            close(emrResult);
            close(emrStatement);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        FetchData fetchData = new FetchData();
        fetchData.fetchAndSave("resources/origin");

    }
}
