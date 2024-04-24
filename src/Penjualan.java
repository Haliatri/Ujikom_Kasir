import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class Penjualan extends javax.swing.JFrame {
    private DefaultTableModel model=null;
    private PreparedStatement stat;
    private ResultSet rs;
    Koneksi k = new Koneksi(); 
    
    public Penjualan() {
        initComponents();
        k.connect();
        tabelkosongdetail();
        tabelpenjualan();
        tabelkosongpelanggan();
        tabelkosongproduk();
        utama();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        txt_tanggal_penjualan.setText(sdf.format(d));
        txt_total_harga.setText("0");
        txt_nama_pelanggan.requestFocus();
    }
    
    public void tabelkosongdetail(){
        model = new DefaultTableModel();
        tabeldetailpenjualan.setModel(model);
        model.addColumn("PenjualaID");
        model.addColumn("ProdukID");
        model.addColumn("JumlahProduk");
        model.addColumn("Harga");
        model.addColumn("Subtotal");
    }
    
    public void tabelpenjualan(){
        try {
            this.stat = k.getCon().prepareStatement("select * from penjualan");
            this.rs = this.stat.executeQuery();
            tabelpenjualan.setModel(DbUtils.resultSetToTableModel(rs));
            System.out.println("Koneksi");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "tabel gagal di load");
        }
    }
    
    public void tabelkosongproduk(){
        model = new DefaultTableModel();
        tabelproduk.setModel(model);
        model.addColumn("ProdukID");
        model.addColumn("NamaProduk");
        model.addColumn("Harga");
        model.addColumn("Stok");
    }
    
    public void tabelkosongpelanggan(){
        model = new DefaultTableModel();
        tabelpelanggan.setModel(model);
        model.addColumn("PelangganID");
        model.addColumn("NamaPelanggan");
        model.addColumn("Alamat");
        model.addColumn("NomorTelepon");
    }
    
    public void utama(){
        txt_pelanggan_id.setText("");
        txt_produk_id.setText("");
        txt_nama_produk.setText("");
        txt_harga.setText("");
        txt_jumlah_produk.setText("");
        autonumber();
    }
    
    public void clear(){
        txt_pelanggan_id.setText("");
        txt_nama_pelanggan.setText("");
        txt_total_harga.setText("");
        txt_tampil.setText("");
    }
    
    class FPenjualan{
    int PenjualanID, TotalHarga, PelangganID;
    String TanggalPenjualan;
    
    public FPenjualan(){
        this.PenjualanID=0;
        this.TanggalPenjualan= txt_tanggal_penjualan.getText();
        this.TotalHarga = Integer.parseInt(txt_total_harga.getText());
        this.PelangganID= 0;
    }
}
    
    public void refreshTable(){
        try {
            this.stat = k.getCon().prepareStatement("select * from detailpenjualan");
            this.rs = this.stat.executeQuery();
            tabeldetailpenjualan.setModel(DbUtils.resultSetToTableModel(rs));
            System.out.println("Koneksi");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "tabel gagal di load");
        }
    }
    
    public void refreshTablepelanggan(){
        try {
            this.stat = k.getCon().prepareStatement("select * from pelanggan where PelangganID like '%" + txt_nama_pelanggan.getText() + "%' OR NamaPelanggan like '%" + txt_nama_pelanggan.getText() + "%'");
            this.rs = this.stat.executeQuery();
            tabelpelanggan.setModel(DbUtils.resultSetToTableModel(rs));
            System.out.println("Koneksi");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Pelanggan Tidak DiTemukan");
        }
    }
    
    private void autonumber() {
        try {
            Connection c = k.getCon();
            Statement s = c.createStatement();
            String sql = "SELECT * FROM penjualan ORDER BY PenjualanID DESC";
            ResultSet r = s.executeQuery(sql);
            if (r.next()) {
                String NoFaktur = r.getString("PenjualanID").substring(2);
                String TR = "" + (Integer.parseInt(NoFaktur) + 1);
                String Nol = "";

                if (TR.length() == 1) {
                    Nol = "000";
                } else if (TR.length() == 2) {
                    Nol = "00";
                } else if (TR.length() == 3) {
                    Nol = "0";
                } else if (TR.length() == 4) {
                    Nol = "";
                }
                txt_id_penjualan.setText("TR" + Nol + TR);
            } else {
                txt_id_penjualan.setText("TR0001");
            }
            r.close();
            s.close();
        } catch (Exception e) {
            System.out.println("autonumber error");
        }
    }

    public void kosong() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    public void inputpenjualan() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();

        String penjID = txt_id_penjualan.getText();
        String tglpenj = txt_tanggal_penjualan.getText();
        String idPLG = txt_pelanggan_id.getText();
        String total = txt_total_harga.getText();

        try {
            Connection c = k.getCon();
            String sql = "INSERT INTO penjualan VALUES (?, ?, ?, ?)";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, penjID);
            p.setString(2, tglpenj);
            p.setString(3, total);
            p.setString(4, idPLG);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("simpan penjualan error");
        }

        try {
            Connection c = k.getCon();
            int baris = tabeldetailpenjualan.getRowCount();

            for (int i = 0; i < baris; i++) {
                String sql = "INSERT INTO detailpenjualan (PenjualanID, ProdukID, JumlahProduk, Harga, Subtotal) VALUES ('" + tabeldetailpenjualan.getValueAt(i, 0) + "','" + tabeldetailpenjualan.getValueAt(i, 1) + "','" + tabeldetailpenjualan.getValueAt(i, 2) + "','" + tabeldetailpenjualan.getValueAt(i, 3) + "','" + tabeldetailpenjualan.getValueAt(i, 4) + "')";
                PreparedStatement p = c.prepareStatement(sql);
                p.executeUpdate();
                p.close();
            }
        } catch (Exception e) {
            System.out.println("simpan detail penjualan error");
        }
        clear();
        utama();
        autonumber();
        kosong();
        txt_tampil.setText("Rp. 0");
    }

    public void refreshTabelProduk() {
        try {
            this.stat = k.getCon().prepareStatement("select * from produk where NamaProduk like '%" + txt_nama_produk.getText() + "%' OR ProdukID like '%" + txt_nama_produk.getText() + "%'");
            this.rs = this.stat.executeQuery();
            tabelproduk.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
    }

    public void totalHarga() {
        int jumlahBaris = tabeldetailpenjualan.getRowCount();
        int totalHarga = 0;
        int jumlahProduk, hargaProduk;
        for (int i = 0; i < jumlahBaris; i++) {
            jumlahProduk = Integer.parseInt(tabeldetailpenjualan.getValueAt(i, 2).toString());
            hargaProduk = Integer.parseInt(tabeldetailpenjualan.getValueAt(i, 3).toString());
            totalHarga = totalHarga + (jumlahProduk * hargaProduk);
        }
        txt_total_harga.setText(String.valueOf(totalHarga));
        txt_tampil.setText("Rp " + totalHarga + ",00");
    }

    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();
        model.addRow(new Object[]{
            txt_id_penjualan.getText(),
            txt_produk_id.getText(),
            txt_jumlah_produk.getText(),
            txt_harga.getText(),
            txt_subtotal.getText()
        });
    }

    public void tambahDetailPenjualan() {
        int jumlah, harga, total;

        jumlah = Integer.valueOf(txt_jumlah_produk.getText());
        harga = Integer.valueOf(txt_harga.getText());
        total = jumlah * harga;

        txt_subtotal.setText(String.valueOf(total));

        loadData();
        totalHarga();
        clear2();
        txt_nama_produk.requestFocus();
    }

    public void clear2() {
        txt_produk_id.setText("");
        txt_nama_produk.setText("");
        txt_harga.setText("");
        txt_jumlah_produk.setText("");
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_id_penjualan = new javax.swing.JTextField();
        txt_nama_pelanggan = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabeldetailpenjualan = new javax.swing.JTable();
        txt_pelanggan_id = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_produk_id = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_harga = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_nama_produk = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        txt_subtotal = new javax.swing.JTextField();
        txt_total_harga = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelpelanggan = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabelproduk = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabelpenjualan = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        txt_tampil = new javax.swing.JTextField();
        txt_tanggal_penjualan = new javax.swing.JTextField();
        txt_jumlah_produk = new javax.swing.JTextField();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Consolas", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PENJUALAN");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 450, 30));

        jLabel2.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel2.setText("HARGA");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 136, 30));

        jLabel3.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel3.setText("TANGGAL PENJUALAN");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 60, -1, 30));

        jLabel4.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel4.setText("TOTAL HARGA");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 290, 100, 30));

        jLabel5.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel5.setText("JUMLAH");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 136, 30));

        txt_id_penjualan.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txt_id_penjualan.setEnabled(false);
        getContentPane().add(txt_id_penjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(202, 60, 180, 30));

        txt_nama_pelanggan.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txt_nama_pelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nama_pelangganKeyReleased(evt);
            }
        });
        getContentPane().add(txt_nama_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 180, 30));

        jButton1.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        jButton1.setText("INPUT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 530, -1, -1));

        tabeldetailpenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabeldetailpenjualan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabeldetailpenjualanMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabeldetailpenjualan);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 400, 710, 107));

        txt_pelanggan_id.setBackground(new java.awt.Color(102, 255, 255));
        txt_pelanggan_id.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        getContentPane().add(txt_pelanggan_id, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 140, 30));

        jLabel6.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel6.setText("ID PENJUALAN");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 59, 136, 30));

        txt_produk_id.setBackground(new java.awt.Color(102, 255, 255));
        txt_produk_id.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        getContentPane().add(txt_produk_id, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 190, 130, 30));

        jLabel7.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel7.setText("SUB TOTAL");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 240, 100, 30));

        txt_harga.setBackground(new java.awt.Color(255, 204, 204));
        txt_harga.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        getContentPane().add(txt_harga, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 240, 180, 30));

        jLabel8.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel8.setText("NAMA PELANGGAN");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 136, 30));

        jLabel9.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel9.setText("NAMA PRODUK");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 136, 30));

        txt_nama_produk.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txt_nama_produk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nama_produkKeyReleased(evt);
            }
        });
        getContentPane().add(txt_nama_produk, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 190, 180, 30));

        jButton4.setText("Tambah Pelanggan");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 100, 140, -1));

        jButton5.setText("Tambah Produk");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 190, 140, -1));

        txt_subtotal.setBackground(new java.awt.Color(255, 204, 204));
        getContentPane().add(txt_subtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 240, 140, 30));

        txt_total_harga.setBackground(new java.awt.Color(255, 204, 204));
        getContentPane().add(txt_total_harga, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 290, 140, 30));

        jButton6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton6.setText("INPUT DETAIL PENJUALAN");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, 220, -1));

        jButton7.setText("DELETE DETAIL PENJUALAN");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 360, 240, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Table Pelanggan");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 30, -1, -1));

        tabelpelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelpelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelpelangganMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabelpelanggan);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 60, 430, 90));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Table Produk");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 170, -1, -1));

        tabelproduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelproduk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelprodukMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabelproduk);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 200, 430, 90));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Table Penjualan");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 320, -1, -1));

        tabelpenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(tabelpenjualan);

        getContentPane().add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 360, 430, 90));

        jButton8.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        jButton8.setText("CETAK NOTA");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 530, -1, -1));

        txt_tampil.setBackground(new java.awt.Color(255, 204, 204));
        txt_tampil.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txt_tampil.setText("Rp.0");
        getContentPane().add(txt_tampil, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 530, 110, 30));

        txt_tanggal_penjualan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        getContentPane().add(txt_tanggal_penjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 60, 140, 30));

        txt_jumlah_produk.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txt_jumlah_produk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_jumlah_produkKeyTyped(evt);
            }
        });
        getContentPane().add(txt_jumlah_produk, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 290, 180, 30));

        setSize(new java.awt.Dimension(1237, 626));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        inputpenjualan();
        tabelpenjualan();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tabeldetailpenjualanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabeldetailpenjualanMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_tabeldetailpenjualanMouseClicked

    private void tabelpelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelpelangganMouseClicked
        // TODO add your handling code here:
        int r = tabelpelanggan.getSelectedRow();
        String PlgID = tabelpelanggan.getValueAt(r, 0).toString();
        String NamaPlg = tabelpelanggan.getValueAt(r, 1).toString();
        String Alamat = tabelpelanggan.getValueAt(r, 2).toString();
        String Tlp = tabelpelanggan.getValueAt(r, 3).toString();
        txt_pelanggan_id.setText(PlgID);
        txt_nama_pelanggan.setText(NamaPlg);
        txt_nama_produk.requestFocus();
    }//GEN-LAST:event_tabelpelangganMouseClicked

    private void tabelprodukMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelprodukMouseClicked
        // TODO add your handling code here:
         int r = tabelproduk.getSelectedRow();
        String ProdukID = tabelproduk.getValueAt(r, 0).toString();
        String NamaPrd = tabelproduk.getValueAt(r, 1).toString();
        String Hrg = tabelproduk.getValueAt(r, 2).toString();
        String Ttl = tabelproduk.getValueAt(r, 3).toString();
        txt_produk_id.setText(ProdukID);
        txt_nama_produk.setText(NamaPrd);
        txt_harga.setText(Hrg);
        txt_jumlah_produk.requestFocus();
    }//GEN-LAST:event_tabelprodukMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        Pelanggan p = new Pelanggan();
        p.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        Produk o = new Produk();
        o.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        tambahDetailPenjualan();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();
        int row = tabeldetailpenjualan.getSelectedRow();
        model.removeRow(row);
        totalHarga();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void txt_nama_produkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nama_produkKeyReleased
        // TODO add your handling code here:
        refreshTabelProduk();
        txt_produk_id.setText("");
    }//GEN-LAST:event_txt_nama_produkKeyReleased

    private void txt_nama_pelangganKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nama_pelangganKeyReleased
        // TODO add your handling code here:
        refreshTablepelanggan();
        txt_pelanggan_id.setText("");
    }//GEN-LAST:event_txt_nama_pelangganKeyReleased

    private void txt_jumlah_produkKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlah_produkKeyTyped
        // TODO add your handling code here:
        char karakter = evt.getKeyChar();
        if (!(((karakter >= '0') && (karakter <= '9') || (karakter == KeyEvent.VK_BACK_SPACE) || (karakter == KeyEvent.VK_BACK_SPACE)))){
            JOptionPane.showMessageDialog(null, "Hanya Boleh Input Angka");
            evt.consume();
        }
    }//GEN-LAST:event_txt_jumlah_produkKeyTyped

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
       // KODING CETAK NOTA
        try {
            File namafile = new File("src/CetakNota.jasper");
            JasperPrint jp = JasperFillManager.fillReport(namafile.getPath(), null, k.getCon());
            JasperViewer.viewReport(jp,false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "GAGAL");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Penjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tabeldetailpenjualan;
    private javax.swing.JTable tabelpelanggan;
    private javax.swing.JTable tabelpenjualan;
    private javax.swing.JTable tabelproduk;
    public static javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_id_penjualan;
    private javax.swing.JTextField txt_jumlah_produk;
    public static javax.swing.JTextField txt_nama_pelanggan;
    public static javax.swing.JTextField txt_nama_produk;
    public static javax.swing.JTextField txt_pelanggan_id;
    public static javax.swing.JTextField txt_produk_id;
    private javax.swing.JTextField txt_subtotal;
    private javax.swing.JTextField txt_tampil;
    private javax.swing.JTextField txt_tanggal_penjualan;
    private javax.swing.JTextField txt_total_harga;
    // End of variables declaration//GEN-END:variables
}
