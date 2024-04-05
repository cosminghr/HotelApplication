package com.example.hotelapplication.entities;

import com.example.hotelapplication.enums.ServiceStatus;
import com.example.hotelapplication.enums.ServiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "services")
public class Services {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID serviceId;
    @Column(name = "serviceName", nullable = false)
    private String serviceName;
    @Column(name = "serviceDescription", nullable = false)
    private String serviceDescription;

    @ManyToMany(mappedBy = "services", cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private List<Rooms> rooms;


}
