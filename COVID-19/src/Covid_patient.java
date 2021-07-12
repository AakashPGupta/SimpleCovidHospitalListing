import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Covid_patient
{
    private JPanel Main;
    private JTextField txtPatientName;
    private JLabel patientName;
    private JLabel patientAge;
    private JLabel patientSex;
    private JTextField txtPatientAge;
    private JTextField txtPatientSex;
    private JTable table1;
    private JButton saveButton;
    private JButton searchButton;
    private JButton updateButton;
    private JTextField txtSearch;
    private JButton deleteButton;
    private JTextField txtPatientStatus;
    private JLabel patientStatus;
    private JScrollPane table_1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Covid Patient");
        frame.setContentPane(new Covid_patient().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    /**
     * Establishing connection with MySQL
     */
    Connection con;
    PreparedStatement pst;
    public void connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/covid", "root","Pravin1965");
            System.out.println("Successs");
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

    }




    /**
     * INSERT BLOCK
     * creating constructor for initializing connection
     * and for the CURD actionlistener
     */

    public Covid_patient()
    {
        connect();
        table_load();
        /**
         * Following code is for creating and saving patient details
         */
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String patient_name,patient_age,patient_sex,patient_status;
                patient_name = txtPatientName.getText();
                patient_age = txtPatientAge.getText();
                patient_sex = txtPatientSex.getText();
                patient_status = txtPatientStatus.getText();

                try {
                    pst = con.prepareStatement("insert into patient(patient_name,patient_age,patient_sex,patient_status)values(?,?,?,?)");      // Sql code for inserting data
                    pst.setString(1, patient_name);
                    pst.setString(2, patient_age);
                    pst.setString(3, patient_sex);
                    pst.setString(4, patient_status);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record is added.");        // For MessageBOx

                    txtPatientName.setText("");
                    txtPatientAge.setText("");
                    txtPatientSex.setText("");
                    txtPatientStatus.setText("");
                    txtPatientName.requestFocus();
                }

                catch (SQLException e1)
                {

                    e1.printStackTrace();
                }

            }

            }
    );



        /**
         * SEARCH BLOCK
         * Following code is for Search the data from the database
         */
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    String patient_id = txtSearch.getText();

                    pst = con.prepareStatement("select patient_name,patient_age,patient_sex,patient_status from patient where patient_id = ?");     // Select Query
                    pst.setString(1, patient_id);
                    ResultSet rs = pst.executeQuery();

                    if(rs.next()==true)                 // this condition is to check that the given ID is True hence the following data will display to their following TextBox
                    {
                        String patient_name = rs.getString(1);
                        String patient_age = rs.getString(2);
                        String patient_sex = rs.getString(3);
                        String patient_status = rs.getString(4);

                        txtPatientName.setText(patient_name);
                        txtPatientAge.setText(patient_age);
                        txtPatientSex.setText(patient_sex);
                        txtPatientStatus.setText(patient_status);

                    }
                    else // if not that this will clear all the TextBox
                    {
                        txtPatientName.setText("");
                        txtPatientAge.setText("");
                        txtPatientSex.setText("");
                        txtPatientStatus.setText("");
                        JOptionPane.showMessageDialog(null,"Invalid Employee No");

                    }
                }
                catch (SQLException ex)
                {
                    ex.printStackTrace();
                }


            }
        });



        /**
         * UPDATE BLOCK
         * Following code is to Update the data in the database
         */
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String patient_name,patient_age,patient_sex,patient_status,patient_id;
                patient_name = txtPatientName.getText();
                patient_age = txtPatientAge.getText();
                patient_sex = txtPatientSex.getText();
                patient_status = txtPatientStatus.getText();
                patient_id=txtSearch.getText();


                try {

                    pst = con.prepareStatement("update patient set patient_name = ?,patient_age = ?,patient_sex = ?,patient_status = ?  where patient_id = ?");     // SQL UPDATE QUERY
                    pst.setString(1, patient_name);
                    pst.setString(2, patient_age);
                    pst.setString(3, patient_sex);
                    pst.setString(4, patient_status);
                    pst.setString(5, patient_id);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record Updated!!!!!");         //MessageBOX
                    table_load();
                    txtPatientName.setText("");
                    txtPatientAge.setText("");
                    txtPatientSex.setText("");
                    txtPatientStatus.setText("");
                    txtPatientName.requestFocus();
                }

                catch (SQLException e1)
                {
                    e1.printStackTrace();
                }



            }
        });


        /**
         * DELETE/DISCHARGE BLOCK
         * Following code is to DELETE the data in the database
         */

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String patient_id;
                patient_id = txtSearch.getText();

                try {
                    int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirmation", JOptionPane.YES_NO_OPTION);   // this is confirmation to prevent accidental discharge of the patient
                    if (confirmed == JOptionPane.YES_OPTION) {
                        pst = con.prepareStatement("delete from patient  where patient_id = ?");            //SQL DELETE QUERY

                        pst.setString(1, patient_id);

                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Following patient has been discharged and their records are deleted!!!!!");
                        table_load();
                        txtPatientName.setText("");
                        txtPatientAge.setText("");
                        txtPatientSex.setText("");
                        txtPatientStatus.setText("");
                        txtPatientName.requestFocus();
                    }
                }

                catch(SQLException e1)
                    {

                        e1.printStackTrace();
                    }
                }



        });
    }




    /**
     * TABLE_SHOW BLOCK
     * Following code is to DISPLAY the TABLE
     */

    void table_load()
    {
        try
        {
            pst = con.prepareStatement("select * from patient");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }






}

