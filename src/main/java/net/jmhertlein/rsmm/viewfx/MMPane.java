package net.jmhertlein.rsmm.viewfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import net.jmhertlein.rsmm.model.*;
import net.jmhertlein.rsmm.viewfx.util.Dialogs;
import net.jmhertlein.rsmm.viewfx.util.FXMLSplitPane;

import javax.swing.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by joshua on 3/11/16.
 */
public class MMPane extends FXMLSplitPane {
    private final Connection conn;
    private final ItemManager items;
    private final QuoteManager quotes;
    private final TurnManager turns;

    private Optional<Integer> geLimitForShownQuoteItem;

    @FXML
    private TableView<Trade> tradeTable;
    @FXML
    private TableColumn<Trade, LocalDateTime> tradeTimeColumn;
    @FXML
    private TableColumn<Trade, Integer> tradeQuantityColumns;
    @FXML
    private TableColumn<Trade, Integer> tradePriceColumn;
    @FXML
    private TableView<Quote> quoteTable;
    @FXML
    private TableColumn<Quote, Timestamp> quoteDateColumn;
    @FXML
    private TableColumn<Quote, Integer> quoteBidColumn;
    @FXML
    private TableColumn<Quote, Integer> quoteAskColumn;
    @FXML
    private TableColumn<Quote, RSInteger> quoteSpreadColumn;
    @FXML
    private TableColumn<Quote, RSInteger> quotePPLColumn;
    @FXML
    private ComboBox<Item> quoteItemChooser;
    @FXML
    private TextField quoteBidField;
    @FXML
    private TextField quoteAskField;
    @FXML
    private TableView<Turn> turnTable;
    @FXML
    private TableColumn<Turn, String> turnItemColumn;
    @FXML
    private TableColumn<Turn, Integer> turnQuantityColumn;
    @FXML
    private TableColumn<Turn, Integer> turnPositionCostColumn;
    @FXML
    private TableColumn<Turn, Integer> turnUsedLimitColumn;
    @FXML
    private TableColumn<Turn, Integer> turnMaxLimitColumn;
    @FXML
    private TableColumn<Turn, RSInteger> turnOpenProfitColumn;
    @FXML
    private TableColumn<Turn, RSInteger> turnClosedProfitColumn;
    @FXML
    private Button closeTurnButton;


    public MMPane(Connection conn) {
        super("/fxml/mmpane.fxml");
        this.conn = conn;

        try {
            items = new ItemManager(conn);
            quotes = new QuoteManager(conn);
            turns = new TurnManager(conn, items, quotes);
        } catch (SQLException | NoSuchItemException | NoQuoteException e) {
            Dialogs.showMessage("Load Error", "Error reading from database.", e);
            throw new RuntimeException("Couldn't load startup data.");
        }

        turnTable.setItems(turns.getOpenTurns());
        quoteItemChooser.setItems(items.getItems());

        map(quoteDateColumn, "quoteTS");
        map(quoteAskColumn, "ask");
        map(quoteBidColumn, "bid");
        map(quoteSpreadColumn, "spread");
        map(quotePPLColumn, "profitPerLimit");
    }

    private static <S, T> void map(TableColumn<S, T> col, String field) {
        col.setCellValueFactory(new PropertyValueFactory<>(field));
    }

    @FXML
    void addBuyTrade(ActionEvent event) {
        System.out.println("CLICK");
    }

    @FXML
    void addQuote(ActionEvent event) {
        int bid, ask;
        try {
            bid = Integer.parseInt(quoteBidField.getText());
            ask = Integer.parseInt(quoteAskField.getText());
        } catch (NumberFormatException ex) {
            Dialogs.showMessage("Invalid Price", "Invalid Price", ex);
            return;
        }

        if (!(bid < ask)) {
            Dialogs.showMessage("Invalid Price", "Invalid Price", "Bid must be < ask");
            return;
        }

        Item selected = quoteItemChooser.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.showMessage("Invalid Item", "Invalid Item", "No item selected.");
            return;
        }

        try {
            quotes.addQuote(selected, bid, ask);
            quoteBidField.setText("");
            quoteAskField.setText("");
        } catch (SQLException ex) {
            Dialogs.showMessage("Error Adding Quote", "Error Adding Quote", ex);
        }
    }

    @FXML
    void addSellTrade(ActionEvent event) {
        System.out.println("CLICK");
    }

    @FXML
    void closeTurn(ActionEvent event) {
        System.out.println("CLICK");
    }

    @FXML
    void openTurn(ActionEvent event) {
        System.out.println("CLICK");
    }

    @FXML
    void showQuotesForItem(ActionEvent event) {
        Optional.ofNullable(quoteItemChooser.getSelectionModel().getSelectedItem()).ifPresent(item -> {
            try {
                quoteTable.setItems(quotes.getQuotesFor(item, new Date(new java.util.Date().getTime())));
            } catch (SQLException e) {
                Dialogs.showMessage("Error Showing Quotes", "Error showing quotes for " + item.getName(), e);
            }
        });
    }

    @FXML
    void syncQuoteToTurn(ActionEvent event) {
        Optional.ofNullable(turnTable.getSelectionModel().getSelectedItem()).ifPresent(turn -> {
            quoteItemChooser.getSelectionModel().clearSelection();
            Optional<Item> item = items.getItem(turn.getItemName());
            item.ifPresent((i) -> {
                quoteItemChooser.getSelectionModel().select(i);
                try {
                    quoteTable.setItems(quotes.getQuotesFor(i, new Date(new java.util.Date().getTime())));
                } catch (SQLException e) {
                    Dialogs.showMessage("Error Showing Quotes", "Error showing quotes for " + i.getName(), e);
                }
            });

        });
    }

}
