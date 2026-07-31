package az.ingress.hrms.entity.person;

import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonPromotion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Persons")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class Person extends WorkflowEntity  {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    public String getFullName() {
        return firstName + " " + lastName;
    }


    @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PersonAddressInfo> addresses = new ArrayList<>();

    @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PersonContactInfo> contacts = new ArrayList<>();


    @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PersonPhoto> photos = new ArrayList<>();

    @OneToMany(
            mappedBy = "person",
            fetch = FetchType.LAZY
    )
    private List<OrderPersonAppointment> appointments =
            new ArrayList<>();

    @OneToMany(
            mappedBy = "person",
            fetch = FetchType.LAZY
    )
    private List<OrderPersonPromotion> promotions = new ArrayList<>();
}