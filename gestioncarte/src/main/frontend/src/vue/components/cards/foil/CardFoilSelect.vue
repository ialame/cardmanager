<template>
    <AdvancedFormSelect :values="Object.keys(foils)" #default="{value: foil}">
        <span>{{foils[foil]}}</span>
    </AdvancedFormSelect>
</template>

<script lang="ts" setup>
import {AdvancedFormSelect} from "@components/form/advanced";
import {foils} from "@components/cards/foil";</script>
