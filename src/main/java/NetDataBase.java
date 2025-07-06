import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

@Named
@RequestScoped
//@Dependent
public class NetDataBase {
	EntityManager entityManager;

    CriteriaBuilder criteriaBuilder;
	public NetDataBase() {
		try {
            entityManager = Persistence.createEntityManagerFactory("GhostNetFishing").createEntityManager();
            criteriaBuilder = entityManager.getCriteriaBuilder();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
	}
	
	public long getNetCount() {
        CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
        cq.select(criteriaBuilder.count(cq.from(FishingNet.class)));
        return entityManager.createQuery(cq).getSingleResult();
    }
	
	public FishingNet getNetAtIndex(int pos) {
        CriteriaQuery<FishingNet> cq = criteriaBuilder.createQuery(FishingNet.class);
        return entityManager.createQuery(cq).setMaxResults(1).setFirstResult(pos).getSingleResult();
    }
	
	public EntityTransaction getAndBeginTransaction() {
        EntityTransaction tran = entityManager.getTransaction();
        tran.begin();
        return tran;
    }
	
	public void add(FishingNet net) {
		entityManager.persist(net);
	}
	
	public List<FishingNet> getAllFishingNet()
	{
		return entityManager.createQuery("SELECT f FROM FishingNet f", FishingNet.class).getResultList();
	}
	
	public static void main(String[] args) {
		NetDataBase ndb = new NetDataBase();
        System.out.println("Wir haben " + ndb.getNetCount() + " Fischernetze.");
    }
}