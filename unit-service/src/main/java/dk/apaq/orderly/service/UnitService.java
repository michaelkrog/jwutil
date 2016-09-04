package dk.apaq.orderly.service;


import dk.apaq.orderly.common.errors.ResourceNotFoundException;
import dk.apaq.orderly.common.filter.UnitIdHeaderFilter;
import dk.apaq.orderly.common.model.Unit;
import dk.apaq.orderly.common.security.Roles;
import dk.apaq.orderly.common.security.SecurityHelper;
import dk.apaq.orderly.common.service.BaseService;
import dk.apaq.orderly.repository.UnitRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UnitService extends BaseService<Unit, UnitRepository> {

    private static final Logger LOG = LoggerFactory.getLogger(UnitService.class);

    @Override
    @PreAuthorize("hasPermission(#entity, 'write')")
    public void delete(Unit entity) {
        LOG.debug("Deleting organization and all its relations [organization={}]", entity);
        super.delete(entity);
    }

    @PreAuthorize(value = "hasPermission(#id, 'organization', 'read')")
    public Unit findOne(String id) {
        LOG.debug("Reading organization [id={}]", id);
        if("current".equals(id)) {
            id = UnitIdHeaderFilter.getCurrentUnitId();
        }
        return getRepository().findOne(id);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public Page<Unit> findAll(Pageable pageable) {
        LOG.debug("Listing all organizations for user {} [pageable={}].", SecurityContextHolder.getContext().getAuthentication(), pageable);

        if (SecurityHelper.isAdmin() || SecurityHelper.isSystem()) {
            return getRepository().findAll(pageable);
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            List<String> roles = new ArrayList<>();
            for(GrantedAuthority a : auth.getAuthorities()) {roles.add(a.getAuthority());}
            
            roles = Roles.resolveRolesByPrefix(roles, Roles.PREFIX_UNITROLE);
            List<String> ids = new ArrayList<>();

            for (String role : roles) {
                ids.add(Roles.getUnitId(role));
            }

            return getRepository().findByIdIn(ids, pageable);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#entity, 'write')")
    public Unit save(Unit entity) {
        LOG.debug("Persisting organizations [organization={}]", entity);
        boolean newEntity = entity.isNew();
        String orgId = UnitIdHeaderFilter.getCurrentUnitId();
        if (orgId != null && entity.isNew()) {
            throw new AccessDeniedException("User is already connected to an organization and cannot create another.");
        }

        if (!newEntity) {
            Unit existing = getRepository().findOne(orgId);
            if (existing == null) {
                throw new ResourceNotFoundException("The referred organization does not exist. [id=" + orgId + "]");
            }

            entity.setId(orgId);
            entity.setTerminated(existing.isTerminated());
        }

        // We first save to make sure that we CAN save using the given properties. (They will be validated.)
        entity = super.save(entity);

        return entity;
    }

    
}
