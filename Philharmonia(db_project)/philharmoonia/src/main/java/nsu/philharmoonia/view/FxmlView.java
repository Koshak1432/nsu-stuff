package nsu.philharmoonia.view;

public enum FxmlView {
    LOGIN {
        @Override
        public String getFxmlFile() {
            return "/fxml/mainScene.fxml";
        }

        @Override
        public String getTitle() {
            return "scene1";
        }
    },
    MAIN {
        @Override
        public String getFxmlFile() {
            return "/fxml/scene2.fxml";
        }

        @Override
        public String getTitle() {
            return "scene 2";
        }
    };



    public abstract String getFxmlFile();
    public abstract String getTitle();
}
