package nsu.philharmoonia.view;

public enum FxmlView {
    MAIN {
        @Override
        public String getFxmlFile() {
            return "/fxml/main.fxml";
        }

        @Override
        public String getTitle() {
            return "Main";
        }
    },
    ANOTHER {
        @Override
        public String getFxmlFile() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }
    };



    public abstract String getFxmlFile();
    public abstract String getTitle();
}
