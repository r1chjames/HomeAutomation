package com.HomeAuto;

import com.HomeAuto.backend.Contact;
import com.HomeAuto.backend.LimitlessLED;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.HomeAuto.backend.ContactService;
import com.vaadin.ui.*;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;

import javax.servlet.annotation.WebServlet;

/* User Interface written in Java.
 *
 * Define the user interface shown on the Vaadin generated web page by extending the UI class.
 * By default, a new UI instance is automatically created when the page is loaded. To reuse
 * the same instance, add @PreserveOnRefresh.
 */
@Title("Dashboard")
@Theme("valo")
public class DashboardUI extends UI {

    TextField search = new TextField();
    Button lightsOn = new Button("Lights On", this::LightsOn);
    ColorPicker colorPicker = new ColorPicker("LimitlessLED Colour");
    Label colorName = new Label("Selected color: " + colorPicker.getColor());
    Button lightsOff = new Button("Lights Off", this::LightsOff);
    Button lightsGreen = new Button("Lights Green", this::LightsGreen);
    Grid contactList = new Grid();
    Button config = new Button("Config");

    // ConfigForm is an example of a custom component class
    ConfigForm configForm = new ConfigForm();

    // ContactService is a in-memory mock DAO that mimics
    // a real-world datasource. Typically implemented for
    // example as EJB or Spring Data based service.
    ContactService service = ContactService.createDemoService();


    /* The "Main method".
     *
     * This is the entry point method executed to initialize and configure
     * the visible user interface. Executed on every browser reload because
     * a new instance is created for each web page loaded.
     */
    @Override
    protected void init(VaadinRequest request) {
        configureComponents();
        buildLayout();
    }


    private void configureComponents() {
         /* Synchronous event handling.
         *
         * Receive user interaction events on the server-side. This allows you
         * to synchronously handle those events. Vaadin automatically sends
         * only the needed changes to the web page without loading a new page.
         */
        config.addClickListener(e -> configForm.edit(new Contact()));
        //lightsOn.addClickListener(e -> LightsOn(this::LightsOn));

        search.setInputPrompt("Search ...");
        search.addTextChangeListener(e -> refreshContacts(e.getText()));
        contactList.setContainerDataSource(new BeanItemContainer<>(Contact.class));
        contactList.setColumnOrder("firstName", "lastName", "email");
        contactList.removeColumn("id");
        contactList.removeColumn("birthDate");
        contactList.removeColumn("phone");
        contactList.setSelectionMode(Grid.SelectionMode.SINGLE);
        contactList.addSelectionListener(e
                -> configForm.edit((Contact) contactList.getSelectedRow()));
        refreshContacts();

        colorPicker.setCaption("LimitlessLED Colour");
        colorPicker.setHistoryVisibility(false);
        colorPicker.setHSVVisibility(false);
        colorPicker.setRGBVisibility(true);
        //colorPicker.

    }


    /* Robust layouts.
     *
     * Layouts are components that contain other components.
     * HorizontalLayout contains TextField and Button. It is wrapped
     * with a Grid into VerticalLayout for the left side of the screen.
     * Allow user to resize the components with a SplitPanel.
     *
     * In addition to programmatically building layout in Java,
     * you may also choose to setup layout declaratively
     * with Vaadin Designer, CSS and HTML.
     */
    private void buildLayout() {
        HorizontalLayout actions = new HorizontalLayout(search, config);
        actions.setWidth("100%");
        search.setWidth("100%");
        actions.setExpandRatio(search, 1);

        VerticalLayout left = new VerticalLayout(actions, lightsOn, lightsOff, lightsGreen, colorPicker, colorName);
        left.addComponent(colorPicker);
        left.addComponent(colorName);
        left.setSizeFull();
        //contactList.setSizeFull();
        left.setExpandRatio(lightsOn, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, configForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);
        // Split and allow resizing
        setContent(mainLayout);

        // Add another component to give feedback from server-side code

    }



    void refreshContacts() {
        refreshContacts(search.getValue());
    }

    private void refreshContacts(String stringFilter) {
        contactList.setContainerDataSource(new BeanItemContainer<>(
                Contact.class, service.findAll(stringFilter)));
        configForm.setVisible(false);
    }

    private void LightsOn(Button.ClickEvent event) {
        LimitlessLED.lightControl(1, "white");
    }

    private void LightsOff(Button.ClickEvent event) {
        LimitlessLED.lightControl(1, "off");
    }

    private void LightsGreen(Button.ClickEvent event) {
        LimitlessLED.lightControl(1, "green");
    }

    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = DashboardUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }


}
