import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
    private GuiController controller;

    public MainFrame(GuiController controller) {
        super("ATM GUI");
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 380);
        setLocationRelativeTo(null);
        setContentPane(new LoginPanel(controller, this));
        setVisible(true);
    }

    public void switchPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }
}
