package com.example.demo;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

@Route("") // localhost:8080/
public class MainView extends Composite<VerticalLayout> {

    private final CompanyRepository companyRepository;

    private Grid<Company> grid = new Grid<>();
    private TextField name = new TextField("Name");
    private TextField website = new TextField("Website");
    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button create = new Button("New", VaadinIcon.PLUS.create());
    private VerticalLayout form = new VerticalLayout(name, website, save);

    private Binder<Company> binder = new Binder<>(Company.class);
    private Company company;

    public MainView(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;

        grid.addColumn(Company::getName).setHeader("Name");
        grid.addColumn(Company::getWebsite).setHeader("Website");
        grid.addSelectionListener(event -> setCompany(grid.asSingleSelect().getValue()));
        updateGrid();

        save.addClickListener(event -> saveClicked());
        create.addClickListener(event -> createClicked());

        getContent().add(grid, create, form);
        binder.bindInstanceFields(this);
        binder.setBean(null);
    }

    private void createClicked() {
        grid.asSingleSelect().clear();
        setCompany(new Company());
    }

    private void setCompany(Company company) {
        this.company = company;
        form.setEnabled(company != null);
        binder.setBean(company);
    }

    private void saveClicked() {
        binder.readBean(company);
        if (company.getId() == null) {
            companyRepository.create(company);
        } else {
            companyRepository.update(company);
        }
        updateGrid();
        Notification.show("Saved!");
    }

    private void updateGrid() {
        grid.setItems(companyRepository.findAll());
    }

}
