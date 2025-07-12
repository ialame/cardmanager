import {ExtractedPokemonCardDTO, PokemonSetDTO} from "@/types";
import {ExtractedCardEntry} from "@components/extraction";
import {initLogger} from "@/logger";
import {buildCardGroups, findGroup} from "@components/cards/pokemon/extracted/list/reassociation/group/logic";
import {ExtractedCardGroup} from "@components/cards/pokemon/extracted/list/reassociation/group/ExtractedCardGroup";
import log from "loglevel";

initLogger("pokemon card groups worker");

let groups: ExtractedCardGroup[] = []

function sendGroups() {
    postMessage(groups);
}

function buildGroups(entries: ExtractedCardEntry<ExtractedPokemonCardDTO>[], sets: PokemonSetDTO[]) {
    const newGroups = buildCardGroups(entries, sets);
    const reviewed = groups.filter(g => g.reviewed);

    if (reviewed.length > 0) {
        newGroups.forEach(group => {
            group.reviewed = group.entries.some(c => findGroup(c, reviewed, sets)?.reviewed);
        });
    }
    groups = newGroups;
    sendGroups();
}

function setReviewed(i: number, reviewed?: boolean) {
    groups[i].reviewed = reviewed ?? true;
    sendGroups();
}

onmessage = function (e) {
    log.trace("Received message", e.data);
    if (e.data.type === "review") {
        setReviewed(e.data.index, e.data.reviewed);
    } else if (e.data.type === "build") {
        buildGroups(e.data.entries, e.data.sets);
    } else {
        log.error("Unknown message type", e.data);
    }
}
