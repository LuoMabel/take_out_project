package restaurantApp.dto;

import lombok.Data;
import restaurantApp.entity.OrderDetail;
import restaurantApp.entity.Orders;

import java.util.List;

@Data
public class OrderDto extends Orders {
        private String userName;
        private String phone;
        private String address;
        private String consignee;
        private List<OrderDetail> orderDetails;
}
