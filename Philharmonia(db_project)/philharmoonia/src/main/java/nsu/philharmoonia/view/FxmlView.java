package nsu.philharmoonia.view;

public enum FxmlView {
    LOGIN {
        @Override
        public String getFxmlFile() {
            return "/fxml/login.fxml";
        }

        @Override
        public String getTitle() {
            return "Авторизация";
        }
    },
    MAIN {
        @Override
        public String getFxmlFile() {
            return "/fxml/mainScene.fxml";
        }

        @Override
        public String getTitle() {
            return "Система городской филармонии";
        }
    };



    public abstract String getFxmlFile();
    public abstract String getTitle();
}
