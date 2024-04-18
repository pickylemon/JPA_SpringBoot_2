package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.items.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
//    @PersistenceContext
//    EntityManager em;
    private final EntityManager em;

    //상품저장
    public void save(Item item){
        if(item.getId() == null){
            em.persist(item); //처음 저장하는 경우 id값이 없다.
        } else {
            em.merge(item); //update와 유사.
        }
    }

    //아이템 하나 조회
    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    //모든 아이템 조회
    public List<Item> findAll(){
        String sql = "select i from Item i";
        return em.createQuery(sql, Item.class).getResultList();
    }
}
