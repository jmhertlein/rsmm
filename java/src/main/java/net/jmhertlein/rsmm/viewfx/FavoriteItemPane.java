package net.jmhertlein.rsmm.viewfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.viewfx.util.Dialogs;
import net.jmhertlein.rsmm.viewfx.util.FXMLBorderPane;

import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by joshua on 5/13/16.
 */
public class FavoriteItemPane extends FXMLBorderPane {

    private final ObservableList<Item> nonFavorites, favorites;
    private final FilteredList<Item> filteredNonFavorites, filteredFavorites;

    private final ItemManager items;

    @FXML
    private ListView<Item> itemsList;
    @FXML
    private ListView<Item> favoritesList;

    @FXML
    private TextField searchFavoritesField;

    @FXML
    private TextField searchItemsField;


    public FavoriteItemPane(ItemManager items) {
        super("/fxml/itempane.fxml");
        this.items = items;

        nonFavorites = FXCollections.observableArrayList();
        favorites = FXCollections.observableArrayList();
        filteredFavorites = new FilteredList<>(favorites);
        filteredNonFavorites = new FilteredList<>(nonFavorites);

        itemsList.setItems(filteredNonFavorites);
        favoritesList.setItems(filteredFavorites);

        favorites.setAll(items.getItems().stream().filter(Item::isFavorite).collect(Collectors.toList()));
        nonFavorites.setAll(items.getItems().stream().filter(i -> !i.isFavorite()).collect(Collectors.toList()));
    }

    @FXML
    void addFavorite() {
        Optional.ofNullable(itemsList.getSelectionModel().getSelectedItem()).ifPresent(i -> {
            try {
                items.setFavorite(i, true);
                nonFavorites.remove(i);
                favorites.add(i);
            } catch (SQLException e) {
                Dialogs.showMessage("Error Setting Favorite", "Error Setting Favorite", e);
            }
        });
    }

    @FXML
    void removeFavorite() {
        Optional.ofNullable(favoritesList.getSelectionModel().getSelectedItem()).ifPresent(i -> {
            try {
                items.setFavorite(i, false);
                favorites.remove(i);
                nonFavorites.add(i);
            } catch (SQLException e) {
                Dialogs.showMessage("Error Setting Favorite", "Error Setting Favorite", e);
            }
        });
    }


    @FXML
    void onFavoritesSearch() {
        filteredFavorites.setPredicate(i -> i.getName().contains(searchFavoritesField.getText()));
    }

    @FXML
    void onItemsSearch() {
        filteredNonFavorites.setPredicate(i -> i.getName().contains(searchItemsField.getText()));
    }

    @FXML
    void onOK() {
        ((Stage) this.getScene().getWindow()).close();
    }
}
