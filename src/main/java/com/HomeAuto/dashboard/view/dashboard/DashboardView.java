package com.HomeAuto.dashboard.view.dashboard;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import com.HomeAuto.dashboard.backend.drivers.*;
import com.google.common.eventbus.Subscribe;
import com.HomeAuto.dashboard.DashboardUI;
//import com.HomeAuto.dashboard.component.TopTenMoviesTable;
import com.HomeAuto.dashboard.domain.DashboardNotification;
import com.HomeAuto.dashboard.event.DashboardEvent.CloseOpenWindowsEvent;
import com.HomeAuto.dashboard.event.DashboardEvent.NotificationsCountUpdatedEvent;
import com.HomeAuto.dashboard.event.DashboardEventBus;
import com.HomeAuto.dashboard.view.dashboard.DashboardEdit.DashboardEditListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;
import jdk.jfr.events.ExceptionThrownEvent;

@SuppressWarnings("serial")
public final class DashboardView extends Panel implements View,
        DashboardEditListener {
//TODO Need to change to lambda
//TODO Need to figure out layout
    public static final String EDIT_ID = "dashboard-edit";
    public static final String TITLE_ID = "dashboard-title";

    private Label titleLabel;
    private NotificationsButton notificationsButton;
    private CssLayout dashboardPanels;
    private final VerticalLayout root;
    private Window notificationsWindow;

    public DashboardView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        DashboardEventBus.register(this);

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        setContent(root);
        Responsive.makeResponsive(root);
        root.addComponent(buildHeader());

        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                DashboardEventBus.post(new CloseOpenWindowsEvent());
            }
        });
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        titleLabel = new Label("Dashboard");
        titleLabel.setId(TITLE_ID);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        Button speech = speechButton();
        notificationsButton = buildNotificationsButton();
        Component edit = buildEditButton();
        HorizontalLayout tools = new HorizontalLayout(speech, notificationsButton, edit);
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }
//TODO Speech still returns error - could replace with https://stackoverflow.com/questions/26485531/google-speech-api-v2
    private Button speechButton() {
        Button result = new Button();
        result.setId(EDIT_ID);
        result.setIcon(FontAwesome.MICROPHONE);
        result.addStyleName("icon-edit");
        result.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        result.setDescription("Speech Recognition");
        result.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    Speech.Listen();
                } catch (Exception e) {
                    System.out.println("Speech Error: " + e);
                }
            }
        });
        return result;
    }

    private NotificationsButton buildNotificationsButton() {
        NotificationsButton result = new NotificationsButton();
        result.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                openNotificationsPopup(event);
            }
        });
        return result;
    }

    private Component buildEditButton() {
        Button result = new Button();
        result.setId(EDIT_ID);
        result.setIcon(FontAwesome.EDIT);
        result.addStyleName("icon-edit");
        result.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        result.setDescription("Edit Dashboard");
        result.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                getUI().addWindow(
                        new DashboardEdit(DashboardView.this, titleLabel
                                .getValue()));
            }
        });
        return result;
    }

    private Component buildContent() {
        dashboardPanels = new CssLayout();
        dashboardPanels.addStyleName("dashboard-panels");
        Responsive.makeResponsive(dashboardPanels);

        dashboardPanels.addComponent(limitlessLED());
        dashboardPanels.addComponent(electronics());
        dashboardPanels.addComponent(switches());

        return dashboardPanels;
    }

    private Component limitlessLED(){
        Panel panel = new Panel("LimitlessLED");
        Layout layout = new HorizontalLayout();
        Button lightsOn = new Button("Lights On", this::LightsOn);
        Button lightsOff = new Button("Lights Off", this::LightsOff);
        Button lightsGreen = new Button("Lights Green", this::LightsGreen);
        //lightsOn.addStyleName(Dashboard.buttonHalo);
        layout.addComponent(lightsOn);
        layout.addComponent(lightsOff);
        layout.addComponent(lightsGreen);
        panel.setContent(layout);
        Component compPanel = createContentWrapper(panel);
        return compPanel;
    }

    private Component electronics(){
        Panel panel = new Panel("Electronics");
        Layout layout = new HorizontalLayout();
        Button tvOn = new Button("TV On", this::TV);
        Button tvOff = new Button("TV Off", this::TV);
        Button devQuery = new Button("Ping", this::DevQuery);
        layout.addComponent(tvOn);
        layout.addComponent(tvOff);
        layout.addComponent(devQuery);
        panel.setContent(layout);
        Component compPanel = createContentWrapper(panel);
        return compPanel;
    }

    private Component switches(){
        Panel panel = new Panel("Switches");
        Layout layout = new HorizontalLayout();
        Button sw01 = new Button("WeMo Switch 1", this::Switch);
        layout.addComponent(sw01);
        panel.setContent(layout);
        Component compPanel = createContentWrapper(panel);
        return compPanel;
    }

    private Component createContentWrapper(final Component content) {
        final CssLayout slot = new CssLayout();
        slot.setWidth("100%");
        slot.addStyleName("dashboard-panel-slot");

        CssLayout card = new CssLayout();
        card.setWidth("100%");
        card.addStyleName(ValoTheme.LAYOUT_CARD);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addStyleName("dashboard-panel-toolbar");
        toolbar.setWidth("100%");

        Label caption = new Label(content.getCaption());
        caption.addStyleName(ValoTheme.LABEL_H4);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        content.setCaption(null);

        MenuBar tools = new MenuBar();
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuItem max = tools.addItem("", FontAwesome.EXPAND, new Command() {

            @Override
            public void menuSelected(final MenuItem selectedItem) {
                if (!slot.getStyleName().contains("max")) {
                    selectedItem.setIcon(FontAwesome.COMPRESS);
                    toggleMaximized(slot, true);
                } else {
                    slot.removeStyleName("max");
                    selectedItem.setIcon(FontAwesome.EXPAND);
                    toggleMaximized(slot, false);
                }
            }
        });
        max.setStyleName("icon-only");
        MenuItem root = tools.addItem("", FontAwesome.COG, null);
        root.addItem("Configure", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                Notification.show("Not implemented yet");
            }
        });
        root.addSeparator();
        root.addItem("Close", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                Notification.show("Not implemented yet");
            }
        });

        toolbar.addComponents(caption, tools);
        toolbar.setExpandRatio(caption, 1);
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

        card.addComponents(toolbar, content);
        slot.addComponent(card);
        return slot;
    }

    private void openNotificationsPopup(final ClickEvent event) {
        VerticalLayout notificationsLayout = new VerticalLayout();
        notificationsLayout.setMargin(true);
        notificationsLayout.setSpacing(true);

        Label title = new Label("Notifications");
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        notificationsLayout.addComponent(title);

        Collection<DashboardNotification> notifications = DashboardUI
                .getDataProvider().getNotifications();
        DashboardEventBus.post(new NotificationsCountUpdatedEvent());

        for (DashboardNotification notification : notifications) {
            VerticalLayout notificationLayout = new VerticalLayout();
            notificationLayout.addStyleName("notification-item");

            Label titleLabel = new Label(notification.getFirstName() + " "
                    + notification.getLastName() + " "
                    + notification.getAction());
            titleLabel.addStyleName("notification-title");

            Label timeLabel = new Label(notification.getPrettyTime());
            timeLabel.addStyleName("notification-time");

            Label contentLabel = new Label(notification.getContent());
            contentLabel.addStyleName("notification-content");

            notificationLayout.addComponents(titleLabel, timeLabel,
                    contentLabel);
            notificationsLayout.addComponent(notificationLayout);
        }

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth("100%");
        Button showAll = new Button("View All Notifications",
                new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        Notification.show("Not implemented yet");
                    }
                });
        showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        showAll.addStyleName(ValoTheme.BUTTON_SMALL);
        footer.addComponent(showAll);
        footer.setComponentAlignment(showAll, Alignment.TOP_CENTER);
        notificationsLayout.addComponent(footer);

        if (notificationsWindow == null) {
            notificationsWindow = new Window();
            notificationsWindow.setWidth(300.0f, Unit.PIXELS);
            notificationsWindow.addStyleName("notifications");
            notificationsWindow.setClosable(false);
            notificationsWindow.setResizable(false);
            notificationsWindow.setDraggable(false);
            notificationsWindow.setCloseShortcut(KeyCode.ESCAPE, null);
            notificationsWindow.setContent(notificationsLayout);
        }

        if (!notificationsWindow.isAttached()) {
            notificationsWindow.setPositionY(event.getClientY()
                    - event.getRelativeY() + 40);
            getUI().addWindow(notificationsWindow);
            notificationsWindow.focus();
        } else {
            notificationsWindow.close();
        }
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

    private void Switch(Button.ClickEvent event) {
        Notification.show("Actuating Switch 1");
        WeMo.ToggleStatus("192.168.0.26");
    }

    private void DevQuery(Button.ClickEvent event) {
        boolean retVal = false;
        try {
            retVal = NetworkAwareness.PingDevice("192.168.0.55");
        }
        catch (IOException e) {
            System.out.println(e);
        }
        if (retVal == false) {
            Notification.show("Device not available");
        }
        else {
            Notification.show("Device available");
        }
    }

    private void TV(Button.ClickEvent event) {
        try {
            String command = "Off";
            Notification.show("Turning Sony BRAVIA TV" + command);
            SonyBravia.SetStatus("http://tv/sony/IRCC?",command);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void enter(final ViewChangeEvent event) {
        notificationsButton.updateNotificationsCount(null);
    }

    @Override
    public void dashboardNameEdited(final String name) {
        titleLabel.setValue(name);
    }

    private void toggleMaximized(final Component panel, final boolean maximized) {
        for (Iterator<Component> it = root.iterator(); it.hasNext();) {
            it.next().setVisible(!maximized);
        }
        dashboardPanels.setVisible(true);

        for (Iterator<Component> it = dashboardPanels.iterator(); it.hasNext();) {
            Component c = it.next();
            c.setVisible(!maximized);
        }

        if (maximized) {
            panel.setVisible(true);
            panel.addStyleName("max");
        } else {
            panel.removeStyleName("max");
        }
    }

    public static final class NotificationsButton extends Button {
        private static final String STYLE_UNREAD = "unread";
        public static final String ID = "dashboard-notifications";

        public NotificationsButton() {
            setIcon(FontAwesome.BELL);
            setId(ID);
            addStyleName("notifications");
            addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            DashboardEventBus.register(this);
        }

        @Subscribe
        public void updateNotificationsCount(
                final NotificationsCountUpdatedEvent event) {
            setUnreadCount(DashboardUI.getDataProvider()
                    .getUnreadNotificationsCount());
        }

        public void setUnreadCount(final int count) {
            setCaption(String.valueOf(count));

            String description = "Notifications";
            if (count > 0) {
                addStyleName(STYLE_UNREAD);
                description += " (" + count + " unread)";
            } else {
                removeStyleName(STYLE_UNREAD);
            }
            setDescription(description);
        }
    }

}
