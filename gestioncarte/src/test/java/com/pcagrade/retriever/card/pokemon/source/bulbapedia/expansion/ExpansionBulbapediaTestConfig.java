package com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepositoryTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import org.eclipse.jgit.util.StringUtils;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import({PokemonSetRepositoryTestConfig.class})
public class ExpansionBulbapediaTestConfig {

    private ExpansionBulbapedia collectionX() {
        var eb = new ExpansionBulbapedia();

        eb.setId(247);
        eb.setName("Collection X");
        eb.setPage2Name("Collection X");
        eb.setTableName("Collection X");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Collection_X_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/060");
        eb.setSet(PokemonSetTestProvider.collectionX());
        return eb;
    }

    private ExpansionBulbapedia rebelClash() {
        var eb = new ExpansionBulbapedia();

        eb.setId(489);
        eb.setName("Rebel Clash");
        eb.setPage2Name("Rebel Clash");
        eb.setTableName("Rebel Clash");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Rebel_Clash_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("001/192");
        eb.setSet(PokemonSetTestProvider.rebelClash());
        return eb;
    }

    private ExpansionBulbapedia xy() {
        var eb = new ExpansionBulbapedia();

        eb.setId(428);
        eb.setName("XY");
        eb.setPage2Name("XY");
        eb.setTableName("XY");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/XY_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("1/146");
        eb.setSet(PokemonSetTestProvider.xy());
        return eb;
    }

    private ExpansionBulbapedia pokemonGOus() {
        var eb = new ExpansionBulbapedia();

        eb.setId(554);
        eb.setName("Pokémon GO");
        eb.setPage2Name("Pokémon GO");
        eb.setTableName("Pokémon GO");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Pok%C3%A9mon_GO_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("001/078");
        eb.setSet(PokemonSetTestProvider.pokemonGOus());
        return eb;
    }

    private ExpansionBulbapedia pokemonGOjp() {
        var eb = new ExpansionBulbapedia();

        eb.setId(555);
        eb.setName("Pokémon GO");
        eb.setPage2Name("Pokémon GO");
        eb.setTableName("Pokémon GO");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Pok%C3%A9mon_GO_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/071");
        eb.setSet(PokemonSetTestProvider.pokemonGOjp());
        return eb;
    }

    private ExpansionBulbapedia lostThunder() {
        var eb = new ExpansionBulbapedia();

        eb.setId(473);
        eb.setName("Lost Thunder");
        eb.setPage2Name("Lost Thunder");
        eb.setTableName("Lost Thunder");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Lost_Thunder_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("1/214");
        eb.setSet(PokemonSetTestProvider.lostThunder());
        return eb;
    }

    private ExpansionBulbapedia superBurstImpact() {
        var eb = new ExpansionBulbapedia();

        eb.setId(343);
        eb.setName("Super-Burst Impact");
        eb.setPage2Name("Super-Burst Impact");
        eb.setTableName("Super-Burst Impact");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Super-Burst_Impact_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/095");
        eb.setSet(PokemonSetTestProvider.superBurstImpact());
        return eb;
    }

    private ExpansionBulbapedia alterGenesis() {
        var eb = new ExpansionBulbapedia();

        eb.setId(364);
        eb.setName("Alter Genesis");
        eb.setPage2Name("Alter Genesis");
        eb.setTableName("Alter Genesis");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Alter_Genesis_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/095");
        eb.setSet(PokemonSetTestProvider.alterGenesis());
        return eb;
    }

    private ExpansionBulbapedia promoDP() {
        var eb = new ExpansionBulbapedia();

        eb.setId(444);
        eb.setName("DP Black Star Promos");
        eb.setPage2Name("DP Black Star Promos");
        eb.setTableName("DP Black Star Promos");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/DP_Black_Star_Promos_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("DP01");
        eb.setSet(PokemonSetTestProvider.promoDP());
        return eb;
    }

    private ExpansionBulbapedia promoXY() {
        var eb = new ExpansionBulbapedia();

        eb.setId(441);
        eb.setName("XY Black Star Promos");
        eb.setPage2Name("XY Black Star Promos");
        eb.setTableName("XY Black Star Promos");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/XY_Black_Star_Promos_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("XY01");
        eb.setSet(PokemonSetTestProvider.promoXY());
        return eb;
    }

    private ExpansionBulbapedia tagAllStars() {
        var eb = new ExpansionBulbapedia();

        eb.setId(365);
        eb.setName("Tag All Stars");
        eb.setPage2Name("Tag All Stars");
        eb.setTableName("Tag All Stars");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Tag_All_Stars_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/173");
        eb.setSet(PokemonSetTestProvider.tagAllStars());
        return eb;
    }

    private ExpansionBulbapedia unbrokenBonds() {
        var eb = new ExpansionBulbapedia();

        eb.setId(478);
        eb.setName("Unbroken Bonds");
        eb.setPage2Name("Unbroken Bonds");
        eb.setTableName("Unbroken Bonds");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Unbroken_Bonds_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("1/214");
        eb.setSet(PokemonSetTestProvider.unbrokenBonds());
        return eb;
    }

    private ExpansionBulbapedia brilliantStars() {
        var eb = new ExpansionBulbapedia();

        eb.setId(537);
        eb.setName("Brilliant Stars");
        eb.setPage2Name("Brilliant Stars");
        eb.setTableName("Brilliant Stars");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Brilliant_Stars_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("001/172");
        eb.setSet(PokemonSetTestProvider.brilliantStars());
        return eb;
    }

    private ExpansionBulbapedia brilliantStarsTG() {
        var eb = new ExpansionBulbapedia();

        eb.setId(540);
        eb.setName("Brilliant Stars");
        eb.setPage2Name("Brilliant Stars");
        eb.setTableName("Trainer Gallery");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Brilliant_Stars_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("TG01/TG30");
        eb.setSet(PokemonSetTestProvider.brilliantStars());
        return eb;
    }

    private ExpansionBulbapedia vmaxClimax() {
        var eb = new ExpansionBulbapedia();

        eb.setId(536);
        eb.setName("VMAX Climax");
        eb.setPage2Name("VMAX Climax");
        eb.setTableName("VMAX Climax");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/VMAX_Climax_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/184");
        eb.setSet(PokemonSetTestProvider.vmaxClimax());
        return eb;
    }

    private ExpansionBulbapedia promoSM() {
        var eb = new ExpansionBulbapedia();

        eb.setId(463);
        eb.setName("SM Black Star Promos");
        eb.setPage2Name("SM Black Star Promos");
        eb.setTableName("SM Black Star Promos");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/SM_Black_Star_Promos_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("SM01");
        eb.setSet(PokemonSetTestProvider.promoSM());
        return eb;
    }

    private ExpansionBulbapedia promoSWSH() {
        var eb = new ExpansionBulbapedia();

        eb.setId(488);
        eb.setName("SWSH Black Star Promos");
        eb.setPage2Name("SWSH Black Star Promos");
        eb.setTableName("SWSH Black Star Promos");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/SWSH_Black_Star_Promos_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("SWSH001");
        eb.setSet(PokemonSetTestProvider.promoSWSH());
        return eb;
    }

    private ExpansionBulbapedia pcgP() {
        var eb = new ExpansionBulbapedia();

        eb.setId(90);
        eb.setName("PCG-P Promotional cards");
        eb.setPage2Name("PCG-P Promotional cards");
        eb.setTableName("PCG-P Promotional cards");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/PCG-P_Promotional_cards_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/PCG-P");
        eb.setSet(PokemonSetTestProvider.pcgP());
        return eb;
    }

    private ExpansionBulbapedia smP() {
        var eb = new ExpansionBulbapedia();

        eb.setId(334);
        eb.setName("SM-P Promotional cards");
        eb.setPage2Name("SM-P Promotional cards");
        eb.setTableName("SM-P Promotional cards");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/SM-P_Promotional_cards_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/SM-P");
        eb.setSet(PokemonSetTestProvider.smP());
        return eb;
    }

    private ExpansionBulbapedia sP() {
        var eb = new ExpansionBulbapedia();

        eb.setId(502);
        eb.setName("S-P Promotional cards");
        eb.setPage2Name("S-P Promotional cards");
        eb.setTableName("S-P Promotional cards");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/S-P_Promotional_cards_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/S-P");
        eb.setSet(PokemonSetTestProvider.sP());
        return eb;
    }

    private ExpansionBulbapedia pikachusNewFriends() {
        var eb = new ExpansionBulbapedia();

        eb.setId(284);
        eb.setName("Pikachu's New Friends");
        eb.setPage2Name("Pikachu's New Friends");
        eb.setTableName("Pikachu's New Friends");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Pikachu%27s_New_Friends_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/004");
        eb.setSet(PokemonSetTestProvider.pikachusNewFriends());
        return eb;
    }

    private ExpansionBulbapedia platinum() {
        var eb = new ExpansionBulbapedia();

        eb.setId(406);
        eb.setName("Platinum");
        eb.setPage2Name("Platinum");
        eb.setTableName("Platinum");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Platinum_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("1/127");
        eb.setSet(PokemonSetTestProvider.platinum());
        return eb;
    }

    private ExpansionBulbapedia galacticsConquest() {
        var eb = new ExpansionBulbapedia();

        eb.setId(312);
        eb.setName("Galactic's Conquest");
        eb.setPage2Name("Galactic's Conquest");
        eb.setTableName("Galactic's Conquest");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Galactic%27s_Conquest_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/096");
        eb.setSet(PokemonSetTestProvider.galacticsConquest());
        return eb;
    }

    private ExpansionBulbapedia greninjaVunionSpecialCardSet() {
        var eb = new ExpansionBulbapedia();

        eb.setId(576);
        eb.setName("V-UNION Special Card Sets");
        eb.setPage2Name("Greninja V-UNION Special Card Set");
        eb.setTableName("Greninja V-UNION Special Card Set");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Greninja_V-UNION_Special_Card_Set_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/013");
        eb.setSet(PokemonSetTestProvider.vunionSpecialCardSets());
        return eb;
    }

    private ExpansionBulbapedia incandescentArcana() {
        var eb = new ExpansionBulbapedia();

        eb.setId(567);
        eb.setName("Incandescent Arcana");
        eb.setPage2Name("Incandescent Arcana");
        eb.setTableName("Incandescent Arcana");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Incandescent_Arcana_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/068");
        eb.setSet(PokemonSetTestProvider.incandescentArcana());
        return eb;
    }

    private ExpansionBulbapedia legendsAwakened() {
        var eb = new ExpansionBulbapedia();

        eb.setId(404);
        eb.setName("Legends Awakened");
        eb.setPage2Name("Legends Awakened");
        eb.setTableName("Legends Awakened");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Legends_Awakened_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("1/146");
        eb.setSet(PokemonSetTestProvider.legendsAwakened());
        return eb;
    }

    private ExpansionBulbapedia templeOfAnger() {
        var eb = new ExpansionBulbapedia();

        eb.setId(165);
        eb.setName("Temple of Anger");
        eb.setPage2Name("Temple of Anger");
        eb.setTableName("Temple of Anger");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Temple_of_Anger_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber(PokemonCardHelper.NO_NUMBER);
        eb.setSet(PokemonSetTestProvider.templeOfAnger());
        return eb;
    }

    private ExpansionBulbapedia gxUltraShiny() {
        var eb = new ExpansionBulbapedia();

        eb.setId(342);
        eb.setName("GX Ultra Shiny");
        eb.setPage2Name("GX Ultra Shiny");
        eb.setTableName("GX Ultra Shiny");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/GX_Ultra_Shiny_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/150");
        eb.setSet(PokemonSetTestProvider.gxUltraShiny());
        return eb;
    }

    private ExpansionBulbapedia deoxys() {
        var eb = new ExpansionBulbapedia();

        eb.setId(390);
        eb.setName("EX Deoxys");
        eb.setPage2Name("EX Deoxys");
        eb.setTableName("EX Deoxys");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/EX_Deoxys_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("1/107");
        eb.setSet(PokemonSetTestProvider.deoxys());
        return eb;
    }

    private ExpansionBulbapedia zacianAndZamazentaBox() {
        var eb = new ExpansionBulbapedia();

        eb.setId(572);
        eb.setName("Zacian + Zamazenta Box");
        eb.setPage2Name("Zacian + Zamazenta Box");
        eb.setTableName("Zacian + Zamazenta Box");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Zacian_%2B_Zamazenta_Box_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/007");
        eb.setSet(PokemonSetTestProvider.zacianAndZamazentaBox());
        return eb;
    }

    private ExpansionBulbapedia scarletEx() {
        var eb = new ExpansionBulbapedia();

        eb.setId(615);
        eb.setName("Scarlet ex");
        eb.setPage2Name("Scarlet ex");
        eb.setTableName("Scarlet ex");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Scarlet_ex_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/078");
        eb.setSet(PokemonSetTestProvider.scarletEx());
        return eb;
    }

    private ExpansionBulbapedia masterKitSideDeck() {
        var eb = new ExpansionBulbapedia();

        eb.setId(122);
        eb.setName("Master Kit");
        eb.setPage2Name("Master Kit");
        eb.setTableName("Side Deck");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Master_Kit_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/012");
        eb.setSet(PokemonSetTestProvider.masterKitSideDeck());
        return eb;
    }

    private ExpansionBulbapedia emerald() {
        var eb = new ExpansionBulbapedia();

        eb.setId(391);
        eb.setName("EX Emerald");
        eb.setPage2Name("EX Emerald");
        eb.setTableName("EX Emerald");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/EX_Emerald_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("1/106");
        eb.setSet(PokemonSetTestProvider.emerald());
        return eb;
    }

    private ExpansionBulbapedia quaxlyAndMimikyuEX() {
        var eb = new ExpansionBulbapedia();

        eb.setId(621);
        eb.setName("ex Starter Set Quaxly & Mimikyu ex");
        eb.setPage2Name("ex Starter Set Quaxly & Mimikyu ex");
        eb.setTableName("ex Starter Set Quaxly & Mimikyu ex");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Ex_Starter_Sets_(TCG)");
        eb.setLocalization(Localization.JAPAN);
        eb.setFirstNumber("001/023");
        eb.setSet(PokemonSetTestProvider.quaxlyAndMimikyuEX());
        return eb;
    }

    private ExpansionBulbapedia scarletAndViolet() {
        var eb = new ExpansionBulbapedia();

        eb.setId(623);
        eb.setName("Scarlet & Violet");
        eb.setPage2Name("Scarlet & Violet");
        eb.setTableName("Scarlet & Violet");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Scarlet_%26_Violet_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("001/198");
        eb.setSet(PokemonSetTestProvider.scarletAndViolet());
        return eb;
    }

    private ExpansionBulbapedia playPokemonPrizePackSeries1() {
        var eb = new ExpansionBulbapedia();

        eb.setId(631);
        eb.setName("Play! Pokémon Prize Pack Series One");
        eb.setPage2Name("Prize Pack Series One");
        eb.setTableName("Play! Pokémon Prize Pack Series One");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Play!_Pok%C3%A9mon_Prize_Pack_Series_One_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("SWSH111");
        eb.setSet(PokemonSetTestProvider.prizePackSeries1());
        return eb;
    }
    private ExpansionBulbapedia trickOrTrade2022() {
        var eb = new ExpansionBulbapedia();

        eb.setId(632);
        eb.setName("Trick or Trade 2022");
        eb.setPage2Name("Trick or Trade 2022");
        eb.setTableName("Trick or Trade 2022");
        eb.setUrl("https://bulbapedia.bulbagarden.net/wiki/Trick_or_Trade_2022_(TCG)");
        eb.setLocalization(Localization.USA);
        eb.setFirstNumber("015/192");
        eb.setSet(PokemonSetTestProvider.trickOrTrade2022());
        return eb;
    }

    @Bean
    public ExpansionBulbapediaRepository expansionBulbapediaRepository() {
        var list = List.of(
                alterGenesis(),
                brilliantStars(),
                brilliantStarsTG(),
                collectionX(),
                deoxys(),
                promoDP(),
                emerald(),
                galacticsConquest(),
                greninjaVunionSpecialCardSet(),
                gxUltraShiny(),
                incandescentArcana(),
                legendsAwakened(),
                lostThunder(),
                masterKitSideDeck(),
                pcgP(),
                pikachusNewFriends(),
                platinum(),
                playPokemonPrizePackSeries1(),
                pokemonGOus(),
                pokemonGOjp(),
                quaxlyAndMimikyuEX(),
                rebelClash(),
                scarletAndViolet(),
                sP(),
                scarletEx(),
                smP(),
                promoSM(),
                superBurstImpact(),
                promoSWSH(),
                tagAllStars(),
                templeOfAnger(),
                trickOrTrade2022(),
                unbrokenBonds(),
                vmaxClimax(),
                xy(),
                promoXY(),
                zacianAndZamazentaBox()
        );

        var expansionBulbapediaRepository = RetrieverTestUtils.mockRepository(ExpansionBulbapediaRepository.class, list, ExpansionBulbapedia::getId);

        Mockito.when(expansionBulbapediaRepository.findAllByOrderByName()).thenReturn(list);
        Mockito.when(expansionBulbapediaRepository.findAllBySetId(Mockito.any(Ulid.class))).then(i -> {
            var arg = i.getArgument(0, Ulid.class);

            return list.stream()
                    .filter(e -> UlidHelper.equals(e.getSet().getId(), arg))
                    .toList();
        });
        Mockito.when(expansionBulbapediaRepository.findAllByUrlAndPage2Name(Mockito.anyString(), Mockito.anyString())).then(i -> {
            var url = i.getArgument(0, String.class);
            var page2Name = i.getArgument(1, String.class);

            return list.stream()
                    .filter(e -> StringUtils.equalsIgnoreCase(e.getUrl(), url) && StringUtils.equalsIgnoreCase(e.getPage2Name(), page2Name))
                    .toList();
        });
        return expansionBulbapediaRepository;
    }

    @Bean
    public ExpansionBulbapediaMapper expansionBulbapediaMapper() {
        return new ExpansionBulbapediaMapperImpl();
    }

    @Bean
    public ExpansionBulbapediaService expansionBulbapediaService() {
        return new ExpansionBulbapediaService();
    }
}
