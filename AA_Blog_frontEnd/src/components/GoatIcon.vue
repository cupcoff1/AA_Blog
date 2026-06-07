<template>
  <svg viewBox="0 0 200 160" fill="none" xmlns="http://www.w3.org/2000/svg">
    <!-- body: fluffy cloud shape -->
    <circle cx="105" cy="105" r="30" fill="var(--bg-secondary)" stroke="currentColor" stroke-width="2"/>
    <circle cx="85" cy="95" r="22" fill="var(--bg-secondary)" stroke="currentColor" stroke-width="2"/>
    <circle cx="125" cy="95" r="22" fill="var(--bg-secondary)" stroke="currentColor" stroke-width="2"/>
    <circle cx="105" cy="85" r="25" fill="var(--bg-secondary)" stroke="currentColor" stroke-width="2"/>
    <circle cx="90" cy="115" r="20" fill="var(--bg-secondary)" stroke="currentColor" stroke-width="2"/>
    <!-- head -->
    <ellipse cx="75" cy="72" rx="20" ry="17" fill="var(--bg-secondary)" stroke="currentColor" stroke-width="2"/>
    <!-- eye -->
    <circle cx="70" cy="68" r="3" fill="currentColor"/>
    <!-- ears -->
    <ellipse cx="65" cy="58" rx="7" ry="3" fill="var(--bg-secondary)" stroke="currentColor" stroke-width="1.5" transform="rotate(-30 65 58)"/>
    <ellipse cx="85" cy="56" rx="7" ry="3" fill="var(--bg-secondary)" stroke="currentColor" stroke-width="1.5" transform="rotate(30 85 56)"/>
    <!-- legs -->
    <line x1="90" y1="130" x2="87" y2="152" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
    <line x1="105" y1="132" x2="103" y2="154" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
    <line x1="118" y1="130" x2="121" y2="152" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
    <line x1="128" y1="125" x2="133" y2="148" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
    <!-- small tail -->
    <circle cx="148" cy="100" r="8" fill="var(--bg-secondary)" stroke="currentColor" stroke-width="1.5"/>
    <!-- mouth/nose -->
    <circle cx="63" cy="73" r="2" fill="currentColor"/>
    <path d="M60 76 Q65 80 70 78" stroke="currentColor" stroke-width="1" stroke-linecap="round" fill="none"/>
  </svg>
</template>
