package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

    public final static String PRICE_PLAN_ID_KEY = "pricePlanId";
    public final static String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PricePlanService pricePlanService;
    private final AccountService accountService;

    public PricePlanComparatorController(PricePlanService pricePlanService, AccountService accountService) {
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
    }

    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        if (!consumptionsForPricePlans.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans.get());

        return consumptionsForPricePlans.isPresent()
                ? ResponseEntity.ok(pricePlanComparisons)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/recommend/{smartMeterId}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(@PathVariable String smartMeterId,
                                                                                           @RequestParam(value = "limit", required = false) Integer limit) {
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        if (!consumptionsForPricePlans.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(consumptionsForPricePlans.get().entrySet());
        recommendations.sort(Comparator.comparing(Map.Entry::getValue));

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }

        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/set-plan/{smartMeterId}")
    public ResponseEntity<String> setPricePlanForSmartMeterId(@PathVariable String smartMeterId,
                                                              @RequestParam(value = "pricePlanId", required = false) String pricePlanId) {
        if(pricePlanService.getMeterReadingService().getReadings(smartMeterId).isEmpty())
            return ResponseEntity.badRequest().body("Invalid Smart Meter ID");
        if (pricePlanId==null) {
            Optional<Map<String, BigDecimal>> pricePlansWithCost = pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);
            accountService.setSmartMeterToPricePlanAccounts(smartMeterId,
                    pricePlansWithCost.get().entrySet().stream().
                            min(Map.Entry.comparingByValue()).map((Map.Entry::getKey)).get());
        } else {
            if (pricePlanService.getPricePlans().stream()
                    .noneMatch(pp -> pp.getPlanName().equals(pricePlanId))) {
                return ResponseEntity.badRequest().body("Invalid Price Plan ID");
            }
            accountService.setSmartMeterToPricePlanAccounts(smartMeterId,pricePlanId);
        }
        return ResponseEntity.ok().build();
    }
}
