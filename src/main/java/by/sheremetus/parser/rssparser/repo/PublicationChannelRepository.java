package by.sheremetus.parser.rssparser.repo;

import by.sheremetus.parser.rssparser.entity.PublicationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicationChannelRepository extends JpaRepository<PublicationChannel, Long> {

    public List<PublicationChannel> findByActiveIsTrue();

}
