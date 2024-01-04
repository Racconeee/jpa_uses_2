package jpabook.jpashop.repository.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders();   //현재 데이터 2개 있음
        //컬렉션을 제외한 데이터를 OrderQueryDto 에 받아놓고 컬렉션만 나중에 합쳐준다.
        result.forEach( o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }


    public List<OrderQueryDto> findAllByDto_optimization() {

        List<OrderQueryDto> result = findOrders();

        List<Long> orderIds = result.stream()
                    .map( o -> o.getOrderId())
                    .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems =  em.createQuery(
                        "select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id , i.name , oi.orderPrice , oi.count) " +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds" , OrderItemQueryDto.class)
                .setParameter("orderIds" , orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        result.forEach( o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }


    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id , i.name , oi.orderPrice , oi.count) "+
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId" , OrderItemQueryDto.class)
                .setParameter("orderId" , orderId)
                .getResultList();

    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderQueryDto(o.id , m.name , o.orderDate , o.status ,d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" , OrderQueryDto.class)
                .getResultList();

    }

    public List<OrderFlatDto> findAllByDto_flat() {

        return em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderFlatDto(o.id, m.name ,o.orderDate , o.status , d.address ,i.name , oi.orderPrice ,oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i" , OrderFlatDto.class)
                .getResultList();


    }
}
