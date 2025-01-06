package ru.andersenlab.infoservice.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.andersenlab.infoservice.domain.model.Address;
import ru.andersenlab.infoservice.domain.model.Address_;
import ru.andersenlab.infoservice.domain.model.City;
import ru.andersenlab.infoservice.domain.model.City_;
import ru.andersenlab.infoservice.domain.model.Office;
import ru.andersenlab.infoservice.domain.model.Office_;
import ru.andersenlab.infoservice.domain.model.Street;
import ru.andersenlab.infoservice.domain.model.Street_;

public class OfficeSpecificationBuilder {

    public static Specification<Office> officeNameContains(String officeName) {
        return (root, query, criteriaBuilder) ->
                officeName != null
                        ? criteriaBuilder.like(root.get(Office_.OFFICE_NAME), "%" + officeName + "%")
                        : null;
    }

    public static Specification<Office> cityNameContains(String cityName) {
        return (root, query, criteriaBuilder) -> {
            if (cityName == null) {
                return null;
            }
            Join<Office, Address> addressJoin = root.join(Office_.ADDRESS, JoinType.LEFT);
            Join<Address, City> cityJoin = addressJoin.join(Address_.CITY, JoinType.LEFT);
            return criteriaBuilder.like(cityJoin.get(City_.CITY_NAME), "%" + cityName + "%");
        };
    }

    public static Specification<Office> streetNameContains(String streetName) {
        return (root, query, criteriaBuilder) -> {
            if (streetName == null) {
                return null;
            }
            Join<Office, Address> addressJoin = root.join(Office_.ADDRESS, JoinType.LEFT);
            Join<Address, Street> streetJoin = addressJoin.join(Address_.STREET, JoinType.LEFT);
            return criteriaBuilder.like(streetJoin.get(Street_.STREET_NAME), "%" + streetName + "%");
        };
    }

    public static Specification<Office> houseNumberEquals(String houseNumber) {
        return (root, query, criteriaBuilder) -> {
            if (houseNumber == null) {
                return null;
            }
            Join<Office, Address> addressJoin = root.join(Office_.ADDRESS, JoinType.LEFT);
            return criteriaBuilder.equal(addressJoin.get(Address_.HOUSE_NUMBER), houseNumber);
        };
    }
}
