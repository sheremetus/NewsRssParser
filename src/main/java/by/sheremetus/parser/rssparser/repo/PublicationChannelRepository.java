package by.sheremetus.parser.rssparser.repo;

import by.sheremetus.parser.rssparser.entity.PublicationChannel;
import by.sheremetus.parser.rssparser.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationChannelRepository extends JpaRepository<PublicationChannel, Long> {
}
