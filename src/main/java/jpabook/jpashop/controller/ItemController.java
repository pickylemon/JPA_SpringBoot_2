package jpabook.jpashop.controller;

import jpabook.jpashop.domain.items.Book;
import jpabook.jpashop.domain.items.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/new")
    public String create(BookForm form, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()) {
            return "redirect:/items/new";
        }

        Book book = form.toBook();
        itemService.saveItem(book);

        long itemId = book.getId();

        redirectAttributes.addAttribute("itemId", itemId);

        return "redirect:/items/{itemId}";
    }

    @GetMapping("/{itemId}")
    public String ItemDetail(Model model, @PathVariable Long itemId){
        Item item = itemService.findOne(itemId);
        BookForm form = BookForm.toBookForm((Book)item);
        model.addAttribute("form", form);
        return "items/itemDetail";
    }

    @GetMapping
    public String ItemList(Model model){
        List<Item> itemList = itemService.findItems();
        List<BookForm> items = new ArrayList<>();
        for (Item item : itemList) {
            Book book = (Book) item;
            items.add(BookForm.toBookForm(book));
        }
        model.addAttribute("items", items);

        return "items/itemList";
    }

    /**
     * 상품 수정
     */
    @GetMapping("/{itemId}/edit")
    public String updateForm(@PathVariable Long itemId, Model model){
        Item item = itemService.findOne(itemId);
        BookForm form = BookForm.toBookForm((Book) item);

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }
    @PostMapping("/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, BookForm bookForm, BindingResult result, RedirectAttributes redirectAttributes){
        //id를 노출하는 것은 위험한 방법(id조작해서 다른 사람의 정보를 변경하려 할 수도 있고)
        //그래서 현재 (로그인된)유저가 이 정보에 대해 권한이 있는지 체크하는 로직이 반드시 필요하다.

        if(result.hasErrors()){
            return "items/updateItemForm"; //다시 수정form으로 + form데이터
        }

        itemService.saveItem(bookForm.toBook());
        //itemRepository의 em.merge가 호출된다.
        //이 예제에서는 merge를 설명하기 위해 merge를 썼지만
        //실무에서는, em.find로 다시 영속성 컨텍스트로 객체를 불러와서
        //업데이트 칠 필드만 업데이트 쳐서 더티 체킹을 이용해 업데이트 되도록 한다.

        //itemService.updateItem(itemId, bookForm);
        //어설프게 컨트롤러에서 Entity를 생성해서 넘기지 말고 그냥 변경할 데이터만 넘기기

        redirectAttributes.addAttribute("itemId", bookForm.getId());
        return "redirect:/items/{itemId}";
        //POST에서는 바로 view를 반환해서는 안된다. 새로고침시 계속 POST요청이 가게 되므로
        //반드시 PRG패턴을 적용해야함.
    }
}
