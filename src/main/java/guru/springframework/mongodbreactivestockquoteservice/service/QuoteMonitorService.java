package guru.springframework.mongodbreactivestockquoteservice.service;

import guru.springframework.mongodbreactivestockquoteservice.client.StockQuoteClient;
import guru.springframework.mongodbreactivestockquoteservice.domain.Quote;
import guru.springframework.mongodbreactivestockquoteservice.repositories.QuoteRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
// implements the applicationListener for contextRefreshedEvents,
// so that this class method can be ran after application starts up, this is an alternative to CommandLineRunner if springboot is not used
public class QuoteMonitorService implements ApplicationListener<ContextRefreshedEvent> {

    private final StockQuoteClient stockQuoteClient;

    private final QuoteRepository quoteRepository;

    public QuoteMonitorService(StockQuoteClient stockQuoteClient, QuoteRepository quoteRepository) {
        this.stockQuoteClient = stockQuoteClient;
        this.quoteRepository = quoteRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        stockQuoteClient.getQuoteStream().log("quote-monitor-service").subscribe(quote -> {
            Mono<Quote> savedQuote = this.quoteRepository.save(quote);
            System.out.println("Stock quote saved: " + savedQuote.block().getId());
        });
    }
}
