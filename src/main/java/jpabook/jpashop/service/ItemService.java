package jpabook.jpashop.service;

import jpabook.jpashop.controller.BookForm;
import jpabook.jpashop.domain.items.Book;
import jpabook.jpashop.domain.items.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional //단순 조회가 아닌 경우에는 메서드 단위로 @Transactional 다시 붙여주기
    public void saveItem(Item item){
        itemRepository.save(item);
    }
    @Transactional
    public void updateItem(Long itemId, BookForm param){
        Item findItem = itemRepository.findOne(itemId);
        //준영속 상태인 객체를 다시 영속 상태로 전환
        Book findBook = (Book) findItem;
        //==여기서부턴 내 생각==//
        //변경된 내용만 업데이트를 하고 싶은데,
        //변경 항목이 고정이 아니라 나중에 비즈니스 요구사항에 따라 바뀔 수 있다면?
        //그때 코드를 수정하지 않으려면.. setter호출을 필드별로 정적으로 하는게 아니라
        //동적으로 setter 호출을 하고 싶은데..

        //아이디어1 (reflection을 써야 할거 같은데 복잡해질듯)
        //param에 동적으로 getter를 호출해서 필드값이 null,0,공백이 아니면
        //findBook의 setter로 그 값을 할당하기

        //updateForm과 saveForm을 분리(입력과 수정 폼의 필드가 다르다면)
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long id){
        return itemRepository.findOne(id);
    }
}
