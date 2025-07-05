<template>
    <Row>
        <Column>
            <YuGiOhSerieSelect v-model="serieFilter" />
        </Column>
        <Column>
            <YuGiOhSetSelect v-bind="$attrs" :values="sets" />
        </Column>
    </Row>
</template>

<script lang="ts" setup>
import {YuGiOhSetSelect, yugiohSetService} from "@components/cards/yugioh/set";
import Row from "@components/grid/Row.vue";
import Column from "@components/grid/Column.vue";
import {YuGiOhSerieSelect} from "@components/cards/yugioh/serie";
import {ref} from "vue";
import {YuGiOhSerieDTO} from "@/types";
import {computedAsync} from "@vueuse/core";
import {isNil} from "lodash";

const serieFilter = ref<YuGiOhSerieDTO>();
const sets = computedAsync(() => yugiohSetService.find(s => isNil(serieFilter.value) || s.serieId === serieFilter.value?.id));
</script>
