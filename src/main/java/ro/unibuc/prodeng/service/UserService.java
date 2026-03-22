package ro.unibuc.prodeng.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.model.UserRank;
import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.response.UserResponse;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse getUserById(String id) throws EntityNotFoundException {
        return toResponse(getUserEntityById(id));
    }

    public UserEntity getUserEntityById(String id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }

    public UserEntity getUserEntityByEmail(String email) throws EntityNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(email));
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Adresa de email exista deja!");
        }
        UserEntity user = new UserEntity(null, request.name(), request.email(), 0, UserRank.BRONZE);
        return toResponse(userRepository.save(user));
    }

    public UserResponse changeName(String id, String newName) throws EntityNotFoundException {
        UserEntity user = getUserEntityById(id);
        UserEntity updated = new UserEntity(user.id(), newName, user.email(), user.xp(), user.rank());
        return toResponse(userRepository.save(updated));
    }

    public void addXpAndSave(UserEntity user, int xpToAdd) {
        int newXp = user.xp() + xpToAdd;
        UserRank newRank = UserRank.BRONZE;
        
        if (newXp >= 300) newRank = UserRank.GOLD;
        else if (newXp >= 100) newRank = UserRank.SILVER;

        UserEntity updatedUser = new UserEntity(user.id(), user.name(), user.email(), newXp, newRank);
        userRepository.save(updatedUser);
        
        if (newRank != user.rank()) {
            System.out.println("🎉 LEVEL UP! Utilizatorul " + user.name() + " a ajuns la rank-ul " + newRank + "!");
        }
    }

    private UserResponse toResponse(UserEntity user) {
        return new UserResponse(user.id(), user.name(), user.email());
    }
}