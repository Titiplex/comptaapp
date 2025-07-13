package com.titiplex.comptaapp.controllers;
import com.titiplex.comptaapp.*;
import com.titiplex.comptaapp.dao.TransactionDao;
import com.titiplex.comptaapp.models.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import java.time.LocalDate;
public class TransactionsController{
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction,LocalDate> dateCol;
    @FXML private TableColumn<Transaction,String> descCol;
    @FXML private TableColumn<Transaction,Number> amtCol;
    @FXML private DatePicker dateField;
    @FXML private TextField descField,amountField;
    @FXML private ChoiceBox<Account> accountChoice;
    @FXML private Button addBtn;
    @FXML private void initialize(){
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        transactionsTable.setItems(DataStore.transactions);
        accountChoice.setItems(DataStore.accounts);
        accountChoice.setConverter(new StringConverter<>(){ public String toString(Account a){return a==null?"":a.getName();} public Account fromString(String s){return null;}});
        dateField.setValue(LocalDate.now());
        addBtn.setOnAction(e->add());
    }
    private void add(){
        Account acc=accountChoice.getValue(); if(acc==null){error("Choisissez un compte");return;}
        String desc=descField.getText(); if(desc.isBlank()){error("Description vide");return;}
        double amt; try{ amt=Double.parseDouble(amountField.getText().replace(',','.')); }catch(NumberFormatException ex){ error("Montant invalide");return;}
        TransactionDao.create(dateField.getValue(),desc,amt,acc.getId());
        descField.clear(); amountField.clear();
    }
    private void error(String m){ new Alert(Alert.AlertType.ERROR,m).showAndWait();}
}