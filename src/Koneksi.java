import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Koneksi {
     private String url = "jdbc:mysql://localhost/kasir_kesha";
    private String Username = "root";
    private String Password = "";
    private Connection con;
    
    public void connect(){
        try {
            con = DriverManager.getConnection(url, Username, Password);
            System.out.println("Koneksi database berhasil");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Koneksi database gagal");
        }
    }
    public Connection getCon(){
        return con;
    }
}
