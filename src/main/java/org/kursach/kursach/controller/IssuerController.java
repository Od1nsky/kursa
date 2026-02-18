package org.kursach.kursach.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.Issuer;
import org.kursach.kursach.service.IssuerService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Named
@ViewScoped
public class IssuerController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(IssuerController.class.getName());
    
    @Inject
    private IssuerService issuerService;
    
    private List<Issuer> issuers;
    private Issuer issuer;
    private Long editId;
    private String searchTerm;
    
    @PostConstruct
    public void init() {
        issuer = new Issuer();
        loadIssuers();
        loadFromRequest();
    }
    
    private void loadIssuers() {
        issuers = issuerService.getAllIssuers();
    }
    
    private void loadFromRequest() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
        String idParam = params.get("id");
        if (idParam != null && !idParam.isBlank()) {
            try {
                editId = Long.valueOf(idParam);
                issuer = issuerService.getIssuerById(editId);
                if (issuer == null) {
                    issuer = new Issuer();
                    logger.warning("Эмитент с ID " + editId + " не найден");
                }
            } catch (NumberFormatException e) {
                logger.warning("Некорректный ID эмитента: " + idParam);
            }
        }
    }
    
    public String save() {
        issuerService.saveIssuer(issuer);
        loadIssuers();
        issuer = new Issuer();
        editId = null;
        return "issuer?faces-redirect=true";
    }
    
    public String edit(Long id) {
        Issuer found = issuerService.getIssuerById(id);
        if (found == null) {
            logger.warning("Попытка редактировать отсутствующего эмитента: " + id);
            return "issuer?faces-redirect=true";
        }
        this.issuer = found;
        this.editId = id;
        return "issuer-edit?faces-redirect=true&id=" + id;
    }
    
    public String delete(Long id) {
        issuerService.deleteIssuer(id);
        loadIssuers();
        return "issuer?faces-redirect=true";
    }
    
    public void search() {
        issuers = issuerService.searchIssuers(searchTerm);
    }
    
    public void resetSearch() {
        searchTerm = null;
        loadIssuers();
    }
    
    public String prepareNew() {
        issuer = new Issuer();
        editId = null;
        return "issuer-edit?faces-redirect=true";
    }
    
    public List<Issuer> getIssuers() {
        return issuers;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public Long getEditId() {
        return editId;
    }

    public void setEditId(Long editId) {
        this.editId = editId;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}