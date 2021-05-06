/*
 * (c) Copyright 2021 Hasan Selman Kara. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package li.selman.dershop.user;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<DerShopUser, Long> {
    Optional<DerShopUser> findOneByActivationKey(String activationKey);

    List<DerShopUser> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<DerShopUser> findOneByResetKey(String resetKey);

    Optional<DerShopUser> findOneByEmailIgnoreCase(String email);

    Optional<DerShopUser> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<DerShopUser> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<DerShopUser> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<DerShopUser> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
