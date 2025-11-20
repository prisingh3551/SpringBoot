package com.priyasingh.ecommerce.service;

import com.priyasingh.ecommerce.exceptions.ResourseNotFoundException;
import com.priyasingh.ecommerce.model.Address;
import com.priyasingh.ecommerce.model.User;
import com.priyasingh.ecommerce.payload.AddressDTO;
import com.priyasingh.ecommerce.repository.AddressRepository;
import com.priyasingh.ecommerce.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImplementation implements AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddress() {
        List<Address> addressList = addressRepository.findAll();

        List<AddressDTO> addressDTOS = addressList.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
        return  addressDTOS;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address savedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourseNotFoundException("Address", "addressId", addressId));

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressByUser(User user) {
        List<Address> addressList = user.getAddresses();

        List<AddressDTO> addressDTOList = addressList.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        return addressDTOList;
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressFromDb = addressRepository.findById(addressId)
                .orElseThrow(() -> new  ResourseNotFoundException("Address", "addressId", addressId));

        addressFromDb.setStreet(addressDTO.getStreet());
        addressFromDb.setBuildingName(addressDTO.getBuildingName());
        addressFromDb.setCity(addressDTO.getCity());
        addressFromDb.setState(addressDTO.getState());
        addressFromDb.setCountry(addressDTO.getCountry());
        addressFromDb.setPincode(addressDTO.getPincode());

        Address updatedAddress = addressRepository.save(addressFromDb);

        User user = addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        Address addressFromDb = addressRepository.findById(addressId)
                .orElseThrow(() -> new  ResourseNotFoundException("Address", "addressId", addressId));

        User user = addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(addressFromDb);

        return "Address deleted successfully with addressId: " + addressId;
    }

}
